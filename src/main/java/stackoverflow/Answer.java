package stackoverflow;

/**
 * Answer object that is attached to a question to calculate score
 * 
 * @author Narasimman
 * 
 */
public class Answer {
  private final int id;
  private final int score;
  private final String body;
  private int userScore;

  public Answer(int id, int score, String body) {
    if (id == 0) {
      throw new IllegalArgumentException("Invalid Id for answer");
    }

    this.id = id;
    this.score = score;
    this.body = body;
  }

  public int getId() {
    return id;
  }

  public int getScore() {
    return score;
  }

  public String getBody() {
    return body;
  }

  public double getWeightedScore() {
    return score / 100;
  }

  public int getUserScore() {
    return userScore;
  }

  public void setUserScore(int userScore) {
    this.userScore = userScore;
  }

  public double getWeightedUserScore() {
    return userScore / 1000;
  }

  @Override
  public String toString() {
    return "Answer [id=" + id + ", score=" + score + ", body=" + body
        + ", userScore=" + userScore + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((body == null) ? 0 : body.hashCode());
    result = prime * result + id;
    result = prime * result + score;
    result = prime * result + userScore;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Answer)) {
      return false;
    }
    Answer other = (Answer) obj;
    if (body == null) {
      if (other.body != null) {
        return false;
      }
    } else if (!body.equals(other.body)) {
      return false;
    }
    if (id != other.id) {
      return false;
    }
    if (score != other.score) {
      return false;
    }
    if (userScore != other.userScore) {
      return false;
    }
    return true;
  }
}
