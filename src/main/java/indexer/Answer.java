package indexer;

public class Answer {

  private final int id;
  private int score;

  public Answer(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  @Override
  public String toString() {
    return "Answer [id=" + id + ", score=" + score + "]";
  }
}
