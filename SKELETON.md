Here is where you will explain your plan for the Walking Skeleton.

We will talk more about this in the future. In summary, the Walking Skeleton is a plan for setting up a minimal version of your tech stack. This is less than a MVP (minimum viable product) as this is not meant to be a product. It is to prove that you are able to integrate the three main components of your application: front end, back end, and database. 

To complete the Skeleton you must be able to interact with your front end, have that interaction be sent to your backend, have something be stored in your database, and return a result back to the front end. This feature does not have to be particularly powerful or meaningful, but you must prove that you can communicate between each component of your application.

Front End:
A very simple text box + submit button (example: "Enter your action")
A response area where the result of the player’s action is displayed

Back End:
Receives the user’s input, processes it, and generates a response
Stretch goal: Use an LLM call to ChatGPT for an AI-generated story

Database:
A single table with fields like:

username
password
session_id
last_action
game_state

Store each user’s most recent action and/or current state.
When the backend receives input, it saves the action and updates the state.

Return Path:
Backend sends the game’s response text back to the frontend.
Frontend displays the response on the screen below the input box.

Proof of Success:
User types "look" in the front end.
Backend receives "look" → saves to DB (last_action = "look").
Backend returns a fixed response (e.g., "You are standing in a dark room.").
Frontend displays the returned text to the user.