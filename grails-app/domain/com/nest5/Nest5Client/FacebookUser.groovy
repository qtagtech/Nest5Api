package com.nest5.Nest5Client

import com.nest5.Nest5Client.SecUser

class FacebookUser {

	long uid
  String accessToken
  Date accessTokenExpires

	static belongsTo = [user: SecUser]

	static constraints = {
		uid unique: true
	}
}
