// GetRecommandRouteResponse.kt
package com.example.MRTAPP.API

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "GetRecommandRouteResponse", strict = false)
data class GetRecommandRouteResponse(
    @field:Element(name = "GetRecommandRouteResult", required = false)
    var getRecommandRouteResult: String? = null
)
