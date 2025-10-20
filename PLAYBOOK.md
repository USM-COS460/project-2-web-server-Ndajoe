## COS 460/540 - Computer Networks
# Project 2: HTTP Server

# Joel Habiyakare

This project is written in Java, using only basic socket and file I/O libraries on macOS.

## How to compile

To compile the project on a Mac, open the Terminal application.
Next, go to the folder where your Java code is saved. For example, if your code is in a folder called “httpServer,” type “cd httpServer” and press Enter to move into that directory. Once you are inside the correct folder, use the Java compiler to build the program by typing “javac serverProject.java” and pressing Enter. This will create a compiled class file that the computer can run.

## How to run

After compiling the project, stay in the same folder inside Terminal.
To start your web server, you need to tell it where your website files are and what port number to use.
Type “java httpServer.serverProject” followed by the path to your website folder (called the document root) and then the port number.
For example, if your website files are in a folder named “www” and you want to use port 8080, you would type:
“java httpServer.serverProject ./www 8080” and press Enter.
After the server starts, open a web browser such as Chrome or Safari and go to “http://localhost:8080”.
You should see your index.html page displayed.

## My experience with this project

This was my first time building a simple web server, and at first it was quite challenging to understand how everything worked together.
I learned how web browsers communicate with servers using HTTP requests and how to send back proper responses with headers and files.
I also learned how to handle more than one user at a time by using threads, which allows the server to serve multiple clients simultaneously.
While testing, I made a small change to my HTML code, I updated the link for the kitten image from “image/kitten-large.jpeg” to just “kitten-large.jpeg.”because at first, the images were not loading when I opened the page in the browser. I learned that this change affects how the browser finds the file: now the browser looks for the image in the same folder as the web page, instead of inside a separate “image” folder.
It helped me understand how relative paths work in web development and how small changes in file paths can affect whether a page or image loads correctly. Overall, this project helped me gain confidence with Java sockets, HTTP basics, and the structure of a web server. Even though it was challenging at first, I learned a lot and now have a much better understanding of how web servers and browsers communicate.
