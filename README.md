# Play-Grammars
A Play Framework 2.5 web app that uses speech recognition grammars to covert long form text into an abbreviated form.
It was created as a programming exercise to learn the fundamentals of the play framework as well as learning how to call
speech recognition grammars from within a scala/play application.

This application converts fully spelled out (English) dates,currencies,times, and numbers into an abbreviated format by 
sending the text string through a compiled speech recognition grammar file: [JSFG](https://www.w3.org/TR/jsgf/)

See Examples below
  

## Installation
Clone the repository and then use sbt to launch the Play Framework/app.

## Usage
Once Play framework starts, open your web-browser to http://localhost:9000 and use the HTTP Form to submit you sentence
 to the speech recognition grammars. Use your browsers back button to return to the form in order to re-enter another
 sentence.


## Examples

For example, 
* if you enter "twenty two dollars and fifty nine cents" the app will detect that you entered a currency and convert it 
to: $22.59
* if you enter "december twenty ninth nineteen eighty three" the app will detect you entered a date and return:
Dec 29 1983
* the time "nine thirty six a.m." would be converted to "9:36AM"
* the number "one hundred fifty two thousand nine hundred and four" would be converted to 152904

Note: The app does not do spell checking, so make sure you spell words correctly or else the grammars will not match 
your text.

### Bugs 
App is a learning exercise and may contain bugs.

## License
Distributed under the Eclipse Public License