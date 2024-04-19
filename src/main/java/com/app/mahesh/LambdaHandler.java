package com.app.mahesh;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayRequest, Context context) {
		StudentService studentService = new StudentService();
		switch (apiGatewayRequest.getHttpMethod()) {

		case "POST":
			return studentService.saveStudent(apiGatewayRequest, context);

		case "PUT":
			if (apiGatewayRequest.getPathParameters() != null) {
				return studentService.updateStudentById(apiGatewayRequest, context);
			}

		case "GET":
			if (apiGatewayRequest.getPathParameters() != null) {
				return studentService.getStudentById(apiGatewayRequest, context);
			}
			return studentService.getStudents(apiGatewayRequest, context);
		case "DELETE":
			if (apiGatewayRequest.getPathParameters() != null) {
				return studentService.deleteStudentById(apiGatewayRequest, context);
			}
		default:
			throw new Error("Unsupported Methods:::" + apiGatewayRequest.getHttpMethod());

		}
	}
}
