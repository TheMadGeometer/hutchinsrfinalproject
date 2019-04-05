package models

case class GoalSummary(
                        goalId: Option[Int],
                        gameId: Int,
                        scoringTeamName: String,
                        periodOfGoal: Int,
                        timeOfGoal: String,
                        goalScorer: Int,
                        assistOne: Int,
                        assistTwo: Int
                      )
