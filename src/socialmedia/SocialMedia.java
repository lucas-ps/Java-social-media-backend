package socialmedia;

import java.io.*;
import java.util.*;

/**
 * Class for socialMedia objects.
 *
 * @author 700037512, 700074221
 */
public class SocialMedia implements SocialMediaPlatform, Serializable  {
    // TODO: delete an account
    // TODO: showchildpostdetail method
    private HashMap<String, Account> accounts;
    private HashMap<Integer, Post> posts;

    // Account/Post getter methods
    public Account getAccount(int ID) throws AccountIDNotRecognisedException {
        for (Account account : accounts.values()) {
            if (account.getId() == ID) {
                return account;
                }
            }
        throw new AccountIDNotRecognisedException("Account ID '" + ID +" not recognised.");
    }

    public Account getAccount(String handle) throws HandleNotRecognisedException {
        if (accounts.containsKey(handle)){
             return accounts.get(handle);
        } else{
            throw new HandleNotRecognisedException("Handle '"+ handle +"'not recognised.");
        }
    }

    public Post getPost(int ID) throws PostIDNotRecognisedException {
        if (posts.containsKey(ID)){
            return posts.get(ID);
        } else{
            throw new PostIDNotRecognisedException("Post ID '"+ ID +"' not recognised.");
        }
    }

    public ArrayList<Post> getPostsByAuthor(Account author) {
        ArrayList<Post> postsByAuthor = new ArrayList();
        for (Post post : posts.values()) {
            if ((post.getAuthor().getHandle()).equals(author.getHandle())) {
                postsByAuthor.add(post);
            }
        }
        return postsByAuthor;
    }

    public void removePosts(ArrayList<Post> posts){
        for (Post post : posts) {
            if (post.getPostType().equals(PostType.COMMENT)) {
                ((Comment) post).makeOrphan();
                this.posts.remove(post.getId());
                // TODO: change message to generic message for parent post
            }
            else if (post.getPostType().equals(PostType.ORIGINAL)) {
                ArrayList<Comment> comments = ((Original) post).getComments();
                for (Comment comment : comments){
                    // TODO: Change comments under this post to have generic post has been removed parent post
                }
                this.posts.remove(post.getId());
            }
            else if (post.getPostType().equals(PostType.ENDORSEMENT)) {
                this.posts.remove(post.getId());
            }
        }
    }

    // Main methods
    @Override
    public int createAccount(String handle, String description) throws IllegalHandleException, InvalidHandleException {
        if (accounts.containsKey(handle)){
            throw new IllegalHandleException("Handle '"+ handle +"' has already been claimed.");
        } else{
            Account newAccount = new Account(handle, description);
            int ID = newAccount.getId();
            accounts.put(handle, newAccount);
            return ID;
        }
    }

    /**
     * The method removes the post from the platform. When a post is removed, all
     * its endorsements should be removed as well. All replies to this post should
     * be updated by replacing the reference to this post by a generic empty post.
     * <p>
     * The generic empty post message should be "The original content was removed
     * from the system and is no longer available.". This empty post is just a
     * replacement placeholder for the post which a reply refers to. Empty posts
     * should not be linked to any account and cannot be acted upon, i.e., it cannot
     * be available for endorsements or replies.
     * <p>
     * The state of this SocialMediaPlatform must be be unchanged if any exceptions
     * are thrown.
     *
     * @param handle handle of post to be removed.
     * @throws PostIDNotRecognisedException if the ID does not match to any post in
     *                                      the system.
     */
    @Override
    public void removeAccount(String handle) throws HandleNotRecognisedException {
        Account accountToBeRemoved = getAccount(handle);
        ArrayList<Post> postsByAuthor = getPostsByAuthor(accountToBeRemoved);
        accountToBeRemoved.clearAccount();
        accounts.remove(handle);
        //TODO: have we removed everything we need too? like in array list as well as hash maps call the remove post method

        // Dealing with posts made by this account
        removePosts(postsByAuthor);
    }

    @Override
    public void updateAccountDescription(String handle, String description) throws HandleNotRecognisedException {
        Account accountToUpdate = getAccount(handle);
        accountToUpdate.setDescription(description);
    }

    @Override
    public int getTotalOriginalPosts() {
        int originalPostCount = 0;
        for (Post post : posts.values()) {
            if (post.getPostType().equals(PostType.ORIGINAL)) {
                originalPostCount++;
            }
        }
        return originalPostCount;
    }

    @Override
    public int getTotalEndorsmentPosts() {
        int endorsementPostCount = 0;
        for (Post post : posts.values()) {
            if (post.getPostType().equals(PostType.ENDORSEMENT)) {
                endorsementPostCount++;
            }
        }
        return endorsementPostCount;
    }

    @Override
    public int getTotalCommentPosts() {
        int commentPostCount = 0;
        for (Post post : posts.values()) {
            if (post.getPostType().equals(PostType.COMMENT)) {
                commentPostCount++;
            }
        }
        return commentPostCount;
    }

    public int createAccount(String handle) throws IllegalHandleException, InvalidHandleException {
        if (accounts.containsKey(handle)){
            throw new IllegalHandleException("Handle '"+ handle +"' has already been claimed.");
        } else{
            Account newAccount = new Account(handle);
            int ID = newAccount.getId();
            accounts.put(handle, newAccount);
            return ID;
        }
    }

    @Override
    public void removeAccount(int id) throws AccountIDNotRecognisedException {
        Account accountToBeRemoved = getAccount(id);

        ArrayList<Post> postsByAuthor = getPostsByAuthor(accountToBeRemoved);
        accountToBeRemoved.clearAccount();
        accounts.remove(accountToBeRemoved.getHandle());

        // Dealing with posts made by this account
        removePosts(postsByAuthor);
    }

    @Override
    public void changeAccountHandle(String oldHandle, String newHandle) throws HandleNotRecognisedException,
            IllegalHandleException, InvalidHandleException {
        Account accountToUpdate = getAccount(oldHandle);
        accountToUpdate.setHandle(newHandle);
        accounts.remove(oldHandle);
        accounts.put(newHandle, accountToUpdate);
    }

    @Override
    public String showAccount(String handle) throws HandleNotRecognisedException {
        Account accountToShow = getAccount(handle);
        return accountToShow.toString();
    }

    @Override
    public int createPost(String handle, String message) throws HandleNotRecognisedException, InvalidPostException {
        Account author = getAccount(handle);
        Original newPost = new Original(author, message); // TODO: Check if the message is validated. It has been we are awesome :)
        author.addPost(newPost);
        posts.put(newPost.getId(), newPost);
        return newPost.getId();
    }

    @Override
    public int endorsePost(String handle, int id) throws HandleNotRecognisedException, PostIDNotRecognisedException,
            NotActionablePostException {
        Account endorsingAccount = getAccount(handle);
        Post post = posts.get(id);
        Account postAuthorAccount = post.getAuthor();
        Endorsement newEndorsement = new Endorsement(endorsingAccount, post.getContents(), post);
        posts.put(newEndorsement.getId(), newEndorsement);
        return newEndorsement.getId();
    }

    @Override
    public int commentPost(String handle, int id, String message) throws HandleNotRecognisedException,
            PostIDNotRecognisedException, NotActionablePostException, InvalidPostException {
        return 0;
    }

    @Override
    public void deletePost(int id) throws PostIDNotRecognisedException {

    }

    @Override
    public String showIndividualPost(int id) throws PostIDNotRecognisedException {
        return null;
    }

    @Override
    public StringBuilder showPostChildrenDetails(int id) throws PostIDNotRecognisedException, NotActionablePostException {
        return null;
    }

    @Override
    public int getMostEndorsedPost() {
        Post mostPopularPost = null;
        for(Post post : posts.values()){
            int popularity = 0;
            if (post.getEndorsementCount() > popularity) {
                popularity = post.getEndorsementCount();
                mostPopularPost = post;
            }
        }
        return mostPopularPost.getId();
    }

    @Override
    public int getMostEndorsedAccount() {
        Account mostPopularAccount = null;
        for (Account account : accounts.values()) {
            int popularity = 0;
            if (account.getEndorsementCount() > popularity) {
                popularity = account.getEndorsementCount();
                mostPopularAccount = account;
            }
        }
        return mostPopularAccount.getId();
    }

    @Override
    public int getNumberOfAccounts(){
        return accounts.size();
    }

    @Override
    public void savePlatform(String filename) throws IOException {
        try{
            FileOutputStream out = new FileOutputStream(filename);
            ObjectOutputStream outStream = new ObjectOutputStream(out);
            outStream.writeObject(this);
            outStream.close();
            out.close();
        } catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    @Override
    public void loadPlatform(String filename) throws IOException, ClassNotFoundException {
        try {
            FileInputStream in = new FileInputStream(filename);
            ObjectInputStream inStream = new ObjectInputStream(in);
            SocialMedia deserialized = (SocialMedia) inStream.readObject();
            this.accounts = deserialized.accounts;
            this.posts = deserialized.posts;
            inStream.close();
            in.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found exception.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    @Override
    public void erasePlatform(){
        accounts = new HashMap<String, Account>();
        posts = new HashMap<Integer, Post>();
        Account.setNewId(0);
        Post.setNewPostId(0);
    }
}