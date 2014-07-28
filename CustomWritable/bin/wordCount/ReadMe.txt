I am executing a custom writable type called as Duo. It is a class that implement the Writable Interface.

input is a text file, in which each record consist of 3 words. for ex:

hello come here
Where are you
How are you

In the Driver class, Mapper makes the first word as key, and create an object of type Duo from the rest of 2 words.