package socialmedia;

import java.io.Serializable;

/**
 * Class for endorsement object.
 *
 * @author 700037512, 700074221
 */
public class Endorsement extends Post implements Serializable {
    private Post parent;
    private PostType type;

    /**
     * Constructor for endorsement objects, adds an endorsement to the parent's endorsement counter.
     * @param author The author object corresponding to who is creating the comment.
     * @param contents The contents string of the comment.
     * @param parent The parent (Original/Comment) object that the comment is commenting on.
     * @throws InvalidPostException  if the message is empty or has more than
     * 	                             100 characters.
     * @throws HandleNotRecognisedException if the handle does not match to any
     * 	                                    account in the system.
     */
    public Endorsement(Account author, String contents, Original parent) throws InvalidPostException,
            HandleNotRecognisedException {
        super(author, contents);
        this.contents = parent.contents;
        this.parent = parent;
        parent.author.addEndorsement(); //see if it works
        this.type = PostType.ENDORSEMENT;
    }

    /**
     * Returns the parent (original/comment) post of the current endorsement post.
     * @return The parent of the current endorsement object.
     */
    public Post getParent() {
        return parent;
    }

    @Override
    /**
     * Returns a string for the endorsement in the form "EP@" + [endorsed account handle] + ": " + [endorsed message]
     * @return The endorsement as a string
     */
    public String toString(){
        String outputStr = "EP@" + this.author.getHandle() + ": " + this.contents;
        return outputStr;
    }
}