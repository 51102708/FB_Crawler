/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author root
 */
public class CommentData
{
    private String commentId;
    private String commentUserId;
    private String commentUserName;
    private String commentMessage;
    private String commentCreateDate;
    private String commentLikeCount;

    public String getCommentUserId() {
		return commentUserId;
	}

	public void setCommentUserId(String commentUserId) {
		this.commentUserId = commentUserId;
	}

	public String getCommentUserName() {
		return commentUserName;
	}

	public void setCommentUserName(String commentUserName) {
		this.commentUserName = commentUserName;
	}

	public void setCommentId(String commentId)
    {
        this.commentId = commentId;
    }

    public void setCommentMessage(String commentMessage)
    {
        this.commentMessage = commentMessage;
    }

    public void setCommentCreateDate(String commentCreateDate)
    {
        this.commentCreateDate = commentCreateDate;
    }

    public void setCommentLikeCount(String commentLikeCount)
    {
        this.commentLikeCount = commentLikeCount;
    }

    public String getCommentId()
    {
        return commentId;
    }

    public String getCommentMessage()
    {
        return commentMessage;
    }

    public String getCommentCreateDate()
    {
        return commentCreateDate;
    }

    public String getCommentLikeCount()
    {
        return commentLikeCount;
    }
}
