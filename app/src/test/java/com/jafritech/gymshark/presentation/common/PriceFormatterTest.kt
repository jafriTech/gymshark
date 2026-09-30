package com.jafritech.gymshark.presentation.common

import org.junit.Assert
import org.junit.Test

class PriceFormatterTest {

    @Test
    fun `whole prices have no decimals`() {
        Assert.assertEquals("£1,000", PriceFormatter.format(1000.0))
        Assert.assertEquals("£50", PriceFormatter.format(50.0))
    }

    @Test
    fun `fractional prices show two decimals`() {
        Assert.assertEquals("£45.50", PriceFormatter.format(45.5))
    }

    @Test
    fun `null price returns null`() {
        Assert.assertNull(PriceFormatter.format(null))
    }
}