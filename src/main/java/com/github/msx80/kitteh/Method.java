package com.github.msx80.kitteh;

import java.lang.annotation.Annotation;

import com.github.msx80.kitteh.producers.annotations.Connect;
import com.github.msx80.kitteh.producers.annotations.Delete;
import com.github.msx80.kitteh.producers.annotations.Get;
import com.github.msx80.kitteh.producers.annotations.Head;
import com.github.msx80.kitteh.producers.annotations.Options;
import com.github.msx80.kitteh.producers.annotations.Post;
import com.github.msx80.kitteh.producers.annotations.Put;

public enum Method
{
	GET(Get.class), POST(Post.class), HEAD(Head.class), PUT(Put.class), DELETE(Delete.class), OPTIONS(Options.class), CONNECT(Connect.class);
	
	private Class<? extends Annotation> relatedAnnotation;

	private Method(Class<? extends Annotation> ann)
	{
		this.relatedAnnotation = ann;
	}

	public Class<? extends Annotation> getRelatedAnnotation() {
		return relatedAnnotation;
	}
	
	
}
