# Research Report
## Balancing Backend and Frontend
### Summary of Work
Benefit of parallel development between backend and frontend:

This research talks that backend and frontend development should be done in parallel rather than separately, because this helps identify problems earlier and avoids major refactoring later. The case study focused on BridgeInTech (BIT), which originally had two endpoints (POST and PUT) for user details. This setup pushed extra business logic to the frontend, making it more complex, slower, and less secure. After refactoring into a single endpoint, the system became cleaner and more efficient. The main takeaway is that backend and frontend cannot be treated in isolation—developers need to understand how both sides work together to keep applications reliable, secure, and easier to maintain.

Discussing Backend For Front-end: 

This research talked about how different clients like web, tablet, and phone apps need different types of data, and sending everything at once causes problems like over-fetching and slow performance. Microservices make this harder since each service would have to handle client-specific data, which creates complexity. The Backend-for-Frontend (BFF) approach fixes this by adding a separate layer that collects data from microservices, filters it, and sends back only what the client needs. This keeps microservices simple, improves performance, and lets each client team manage their own BFF as their needs change.

#### Code snapshots (BridgeInTech case – original split endpoints)

**Create Additional Info (`POST`)**
```python
@classmethod
@users_ns.doc("create_user_additional_info")
@users_ns.response(
    HTTPStatus.CREATED, f"{messages.ADDITIONAL_INFO_SUCCESSFULLY_CREATED}"
)
@users_ns.response(
    HTTPStatus.BAD_REQUEST,
    f"{messages.USER_ID_IS_NOT_VALID}\n"
    f"{messages.IS_ORGANIZATION_REP_FIELD_IS_MISSING}\n"
    f"{messages.TIMEZONE_FIELD_IS_MISSING}"
    f"{messages.UNEXPECTED_INPUT}"
)
@users_ns.response(
    HTTPStatus.FORBIDDEN, f"{messages.USER_ID_IS_NOT_RETRIEVED}"
)
@users_ns.response(
    HTTPStatus.INTERNAL_SERVER_ERROR, f"{messages.INTERNAL_SERVER_ERROR}"
)
@users_ns.expect(auth_header_parser, user_extension_request_body_model, validate=True)
def post(cls):
    """
    Creates user additional information
    A user with valid access token can use this endpoint to add additional 
    information to their own data. The endpoint takes any of the given 
    parameters (is_organization_rep (true or false value), timezone (with 
    value as per Timezone Enum Value) and additional_info (dictionary of 
    phone, mobile and personal_website)). The response contains a success 
    or error message. This request only accessible once user retrieve 
    their user_id by sending GET /user/personal_details.
    """

    token = request.headers.environ["HTTP_AUTHORIZATION"]
    is_wrong_token = validate_token(token)
    
    if not is_wrong_token:
        data = request.json
        if not data:
            return messages.NO_DATA_FOR_UPDATING_PROFILE_WAS_SENT

        is_field_valid = expected_fields_validator(data, user_extension_request_body_model)
        if not is_field_valid.get("is_field_valid"):
            return is_field_valid.get("message"), HTTPStatus.BAD_REQUEST
        
        is_not_valid = validate_update_additional_info_request(data)
        if is_not_valid:
            return is_not_valid, HTTPStatus.BAD_REQUEST

        return UserExtensionDAO.create_user_additional_info(data)
         
    return is_wrong_token

### Motivation
I needed to do this because my role is to make sure that frontend and backend work together. This helps me understand more of the concepts and how to approach them.
### Time Spent
80 minutes
### Results
<!--Explain what you learned/produced/etc. This section should explain the
important things you learned so that it can serve as an easy reference for yourself
and others who could benefit from reviewing this topic. Include your sources as
footnotes. Make sure you include the footnotes where appropriate e.g [^1]-->

From my research, I learned that backend and frontend development should be done in parallel so problems can be caught early and code doesn’t need major re-factoring later. In the BridgeInTech case, having two separate endpoints for creating and updating user details pushed too much logic to the frontend, but refactoring into a single endpoint made the system cleaner, faster, and more secure. I also learned about the Backend-for-Frontend (BFF) approach, which solves the issue of different clients (like web, tablet, and mobile) needing different data. Instead of each microservice customizing responses, the BFF collects and filters data before sending it to the client, reducing complexity and improving performance. Overall, both articles showed me how important it is to think about backend and frontend together and use the right architecture to keep apps efficient and easy to maintain. 

### Sources
<!--list your sources and link them to a footnote with the source url-->
- Placeholder1[^1]
- Placeholder2[^2]
- Placeholder3[^3]
- Placeholder4[^4]
- And so on...
[^1]: [www.google.com](https://medium.com/@mtreacy002/benefit-of-parallel-development-between-backend-and-frontend-7fa5bf2289b5)
[^2]: [www.google.com](https://blog.frankel.ch/backend-for-frontend/)
[^3]: www.google.com
[^4]: www.google.com