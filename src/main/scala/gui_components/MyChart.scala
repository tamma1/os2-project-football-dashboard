package gui_components

import scalafx.scene.Node

trait MyChart extends Node:

  def updateData(leagueID: Int, seasonID: Int, clubID: Int, dataSet: String): Unit

