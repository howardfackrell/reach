package controllers

import play.api.Routes
import play.api.libs.iteratee.{Enumerator, Iteratee, Concurrent}
import play.api.mvc.{WebSocket, Action, Controller}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.data._
import play.api.data.Forms._
import reach.Comp

/**
 * Created by howard.fackrell on 6/22/15.
 */
object Competition extends Controller {

  var competitions = Map[String, Comp](
    "stuff" -> new Comp("stuff", Map("howard.fackrell@octanner.com" -> 5, "karen.thorup@octanner.com" -> 6)),
    "things" -> new Comp("things", Map("cameron.shellum@octanner.com" -> 2, "sue.ure@octanner.com" -> 3))
  )

  var broadcasts = Map[String, (Enumerator[String] , Concurrent.Channel[String])]()

  def list = Action {
    Ok(views.html.competitions(competitions))
  }

  def create = Action {
    Ok("create")
  }

  def display(id : String) = Action { request =>
    println(s"and the name is $id")
    Ok(views.html.competition(competitions.get(id).get, contestantForm)(request))
  }

  def score(id : String, email : String) = Action {
    competitions.get(id) match {
      case Some(competition) => {
        competitions = competitions.updated(id, competition.inc(email, 1))
        broadcasts.get(id) match {
          case Some((out, channel)) => channel.push(competitions.get(id).get.participants.toString)
          case None => {}
        }
        Ok(s"Thank you for helping $email in the $id competition")
      }

      case None =>
        Ok(s"Sorry, the competition $id has ended.")
    }
  }

  case class Contestant(email : String)
  val contestantForm = Form(
    mapping("email" -> text)
      (Contestant.apply)(Contestant.unapply)
  )

  def addParticipant(id : String) = Action { implicit request =>
    println("going to bind the form")
    contestantForm.bindFromRequest.fold(
      formWithErrors => {
        println("something bad happened binding the form")
        BadRequest("That was a bad request")
      },
      contestantData => {
        println("binding ")
        competitions.get(id) match {
          case Some(competition) => {
            competitions = competitions.updated(id, competition.addParticipant(contestantData.email))
          }
          case None => {}
        }
        val url = routes.Competition.display(id).url
        println("and the url is " + url)
        Redirect(url)
      }
    )
  }


  def watch() = WebSocket.using[String] { request =>

    val id  = request.getQueryString("id").get
    val in = Iteratee.foreach[String](println)


    Concurrent.broadcast[String]
    val (out, channel) = broadcasts.get(id) match {
      case Some((out, channel)) => (out, channel)
      case None => {
        val broadcast@(o, c) = Concurrent.broadcast[String]
        broadcasts = broadcasts + (id -> broadcast)
        broadcast
      }
    }

    channel.push(competitions.get(id).get.participants.toString)

    (in, out)
  }
}
