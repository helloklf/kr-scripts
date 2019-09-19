package com.omarea.krscript.model

import com.omarea.krscript.config.ActionParamInfo
import java.util.ArrayList

class ActionInfo : ConfigItemBase() {
    var script: String? = null
    var start: String? = null
    var params: ArrayList<ActionParamInfo>? = null
}
