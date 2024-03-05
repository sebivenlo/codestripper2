# Assignment greeter

Objectives
. Dependency injection to test with time based resources: a clock.

Your task is to create a class that greets based on the time of day.

The time of day (morning, afternoon, eveneing or night) should be determined 
by reading a clock.

kava.util.Clock is an interface that also provided factory methods to create 
common clocks such as a clock you can 'look at' for the time of day referred 
to as a wall-clock. It also has clock implementations that help testing, because
you can set these clocks to a specific time to enable testing.
You can even make them tick with steps you decide, so that tests that should
test the functionality of a class that say should change every quarter of an hour.

Tick the clock ate the speed of the test instead of the real time, you can test this
in miliseconds instead of having to wait hours for the test to complete.

 Assignment.

Make the tests work, and make the greeter class work.


![Inspiration for this task](images/clockpostcard.png)
