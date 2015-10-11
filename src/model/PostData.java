/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.HashMap;

/**
 *
 * @author root
 */
public class PostData
{
    private String postId;
    private String postUser;
    private String postMessage;
    private String postCreateDate;
    private HashMap<String, String> postLikeList;

    public void setPostId(String postId)
    {
        this.postId = postId;
    }

    public void setPostUser(String postUser)
    {
        this.postUser = postUser;
    }

    public void setPostMessage(String postMessage)
    {
        this.postMessage = postMessage;
    }

    public void setPostCreateDate(String postCreateDate)
    {
        this.postCreateDate = postCreateDate;
    }

    public void setPostLikeList(HashMap<String, String> postLikeList)
    {
        this.postLikeList = postLikeList;
    }

    public String getPostId()
    {
        return postId;
    }

    public String getPostUser()
    {
        return postUser;
    }

    public String getPostMessage()
    {
        return postMessage;
    }

    public String getPostCreateDate()
    {
        return postCreateDate;
    }

    public HashMap<String, String> getPostLikeList()
    {
        return postLikeList;
    }
    
}
