package stackoverflow;

import java.util.Comparator;

/**
 * A comparator to order the posts based on the final score calculated
 * 
 * @author Manasa
 */
public class PostComparator implements Comparator<Post> {
  public int compare(Post p1, Post p2) {
    return (p1.getFinalScore() > p2.getFinalScore()) ? -1 : 1;
  }
}