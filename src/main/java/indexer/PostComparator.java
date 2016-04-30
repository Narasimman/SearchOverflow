package indexer;

import java.util.Comparator;

public class PostComparator  implements Comparator<Post>{
	public int compare(Post p1, Post p2)
    {
        // Assume neither string is null. Real code should
        // probably be more robust
        // You could also just return x.length() - y.length(),
        // which would be more efficient.
        if (p1.getfinalScore() < p2.getfinalScore())
        {
            return 1;
        }
        else
        {
        	return -1;
        }
    }
}

