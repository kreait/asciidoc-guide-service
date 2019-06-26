package com.kreait.docs

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GuideController {

    @GetMapping("guides")
    fun guides() {

    }

    @GetMapping("guides/{guideId}")
    fun guide(@PathVariable("guideId") guide: String) {

    }
}
