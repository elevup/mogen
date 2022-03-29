package com.elevup

import com.elevup.model.ComposerConfig

/**
 * Class that eventually is able to synthesize code in some (programming) language
 */
interface Composer {

    /**
     * Configuration bound to current instance. It alters output format.
     */
    val config: ComposerConfig

}
