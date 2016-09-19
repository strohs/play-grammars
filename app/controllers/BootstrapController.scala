package controllers

import play.api.mvc.{Action, Controller}

/**
 * controller for playing around with bootstrap functionality
 * User: Cliff
 * Date: 5/18/2016
 * Time: 5:48 PM
 */

class BootstrapController extends Controller {

  //show a basic bootstrap hello world page to make sure javascript and css are loading
  def hello = Action {
    Ok( views.html.bootstraphw() )
  }

}
