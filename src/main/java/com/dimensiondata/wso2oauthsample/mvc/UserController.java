/**
 * UserController.java
 *
 * @author jason
 * @version 1.0
 * @since 11Nov.,2016
 */
package com.dimensiondata.wso2oauthsample.mvc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author jason
 *
 */
@Controller
@RequestMapping("/api/user")
public class UserController {

	@GetMapping("/{userId}")
	public ResponseEntity<String> home(@PathVariable String userId) {
		return new ResponseEntity<String>(String.format("Hello %s", userId), HttpStatus.OK);
	}
	
	@GetMapping("/hello")
	public ResponseEntity<String> hello() {
		return new ResponseEntity<String>("Hello message from api server.", HttpStatus.OK);
	}

	@RequestMapping(value = "/claims", method = RequestMethod.GET)
	public ResponseEntity<List<String>> claims() {
		List<String> claims = new ArrayList<String>();
		claims.add("Test claim");
		return new ResponseEntity<List<String>>(claims, HttpStatus.OK);
	}
}
