package de.htwg.se.othello.aview.gui

import java.awt.{Color, GridLayout, RenderingHints}

import de.htwg.se.othello.controller.controllerComponent.ControllerInterface
import de.htwg.se.othello.model.Player
import javax.swing.ImageIcon
import javax.swing.border.LineBorder
import javax.imageio.ImageIO

import scala.concurrent.{ExecutionContext, Future}
import scala.swing.event.MouseClicked
import scala.swing.{BorderPanel, BoxPanel, Dimension, FlowPanel, Font, Graphics2D, GridPanel, Label, Orientation}

class OperationPanel(controller: ControllerInterface, Hoehe: Int) extends  FlowPanel{
  val operationsides = 200
  background = Color.LIGHT_GRAY
  preferredSize = new Dimension(operationsides, Hoehe)

  def presentPlayer: BoxPanel =  new BoxPanel(Orientation.Vertical){
    if(controller.isReady) {
      contents += playerWhite
    } else {
      contents += playerBlack
    }
  }
  def playerWhite: Label = new Label()  {
    icon = new ImageIcon(getClass.getResource(s"resources/2.png"))
    foreground = new Color(200, 200, 200)
  }
  def playerBlack: Label = new Label()  {
    icon = new ImageIcon(getClass.getResource(s"resources/1.png"))
    foreground = new Color(200, 200, 200)
  }

  def Undo: BoxPanel = new BoxPanel(Orientation.Horizontal) {
      contents += new Label{
        text = "Undo"
      }

  }

  def Redo: BoxPanel = new BoxPanel(Orientation.Horizontal) {
    contents += new Label{
      text = "Redo"
    }
  }

  contents += presentPlayer
  contents += Undo
  contents += Redo



}