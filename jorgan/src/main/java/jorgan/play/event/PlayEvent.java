/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.play.event;

import java.util.*;

import jorgan.disposition.Element;
import jorgan.play.*;

/**
 * Event of a player.
 */
public class PlayEvent extends EventObject {

  private Element       element;
  private PlayerProblem problem;

  public PlayEvent(OrganPlay source, Element element) {
    this(source, element, null);
  }

  public PlayEvent(OrganPlay source, Element element, PlayerProblem problem) {
    super(source);
    
    this.element  = element;
    this.problem  = problem;
  }

  public Element getElement() {
    return element;
  }
  
  public PlayerProblem getProblem() {
    return problem;
  }
}