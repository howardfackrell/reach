package reach

/**
 * Created by howard.fackrell on 6/22/15.
 */
class Comp(val id : String, val participants : Map[String, Int]) {
  def addParticipant(email : String) =  {
    if (participants.contains(email) ) {
      this
    } else {
      new Comp(id, participants + (email -> 0))
    }
  }

  def removeParticipant(email : String) = {
    new Comp(id, participants - email)
  }

  def inc(email : String, amt : Int) = {
    val score = participants.get(email).getOrElse(0)
    new Comp(id, participants updated (email , score + amt))
  }
}

object Competition {
}
