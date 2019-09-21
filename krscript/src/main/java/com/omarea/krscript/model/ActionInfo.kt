package com.omarea.krscript.model

import com.omarea.krscript.config.ActionParamInfo
import java.util.ArrayList

class ActionInfo : ConfigItemBase() {
    var getState: String? = null
    var setState: String? = null
    var params: ArrayList<ActionParamInfo>? = null
}
