
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import data_processing.*

// Test if API connection is working properly.
class DataTests extends AnyFlatSpec with Matchers:

  val leagueIDs = Array(39, 140, 78, 135, 61)
  val numOfResults = Array(20, 20, 18, 20, 20)

  "GetLeagueData.teams" should "contain right number of results." in {

    for (id, n) <- leagueIDs zip numOfResults do
      assert(LeagueData.getLeagueData(id, 2022).results === n)
  }

  "GetClubData" should "contain right number of results." in {
    assert(ClubData.getClubData(39, 2019, 33).results === 11)
  }