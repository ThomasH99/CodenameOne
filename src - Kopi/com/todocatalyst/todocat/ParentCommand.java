/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.todocatalyst.todocat;

import com.codename1.ui.Command;

/**
 * see: http://lwuit.blogspot.com/search?q=submenus
 *
* Allows building a hierarchy of components (eg submenus)
*
* @author Shai Almog
*/
public class ParentCommand extends Command {
  private Command[] children;
  public ParentCommand(String name, Command[] children) {
      super(name);
      this.children = children;
  }

  public Command[] getChildren() {
      return children;
  }
}