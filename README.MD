# Ferdinand Haaben Final Project

<a href ="https://github.com/Ferdinand737/COSC310-Individual-Project.git"> Github repository link</a>

## ROLE AND TOPICS

<p>
This project is a chatbot that plays the role of a friend.
</p>
<p>
The conversation topics are
<ul>
<li>favorite food, hobbies, books, sports.</li>
<li>sports</li>
</ul>
</p>




## RUN INSTRUCTIONS

<p>Run the file named Ferdinand_Haaben_Individual_Chatbot.jar</p>
<p>Make sure that the Knowledge.dat file is in the same folder as the chatbot.jar file</p> 
<p>The jar file can be found at this <a href="https://drive.google.com/drive/folders/1cm9XKGfR2VE56yod4Qd6UOahiBAvwxvg?usp=sharing"> google drive link.</a></p>

### Libraries used
<ul>
<li><a href="https://jsoup.org/download">jsoup</a></li>
<li><a href="https://stanfordnlp.github.io/CoreNLP/">StanfordNLP Library</a></li>
</ul>



### Chatbot JAVA DOC
<p>There is a javadoc in my repository. It is located in the doc folder.</p>

<p>Java Doc contains everything about the Chatbot members.</p>

## Features added since A3

<ul>
<li>

### General

</li>

<ol>
<li>The bot will check if it has the appropriate response in its hashmap.</li>
<li>If the response cannot be found in the hashmap, then the bot will use wikipedia and google places.</li>
<li>If wikipedia and google places do not return a response then the bot will use word matching. (This is how the bot worked in A3)</li>
<li>Everything else is the same as A3</li>
</ol>
<br>

</ul>

<ul>
<li>

### Wikipedia API
<ul>
<li>The bot will google search the input string with wikipedia on the end and return the first sentance of the wikipedia page using the wikipedia API</li>
</ul>
</li>


<li>

### Google Places API
<ul>
<li>The bot will use an input string to search google places and return the place_id of the search result</li>

<li>The bot uses the place_id to ind the address and geographical coordinates of the input string</li>

</ul>
</li>
</ul>


