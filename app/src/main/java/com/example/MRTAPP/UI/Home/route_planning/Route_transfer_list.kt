package com.example.MRTAPP.UI.Home.route_planning

data class Route_transfer_list(
    val TransferStation:String,
    val Transition_station:List<Route_transition_station_list>
)
