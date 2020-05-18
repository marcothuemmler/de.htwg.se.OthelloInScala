package de.htwg.se.othello

import boardComponent.boardBaseImpl.Board
import boardComponent.{BoardFactory, BoardInterface}
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder
import de.htwg.se.othello.controller.controllerComponent.ControllerInterface
import de.htwg.se.othello.controller.controllerComponent.controllerBaseImpl.Controller
import de.htwg.se.othello.model.fileIOComponent.{FileIOInterface, fileIoXmlImpl}
import net.codingwell.scalaguice.ScalaModule

class OthelloModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    install(new FactoryModuleBuilder()
      .implement(classOf[BoardInterface], classOf[Board])
      .build(classOf[BoardFactory]))
    bind[ControllerInterface].to[Controller]
    bind[FileIOInterface].to[fileIoXmlImpl.FileIO]
  }
}
