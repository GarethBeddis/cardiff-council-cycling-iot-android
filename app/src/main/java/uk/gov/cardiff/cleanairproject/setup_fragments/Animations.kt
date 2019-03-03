package uk.gov.cardiff.cleanairproject.setup_fragments

import android.support.v4.view.animation.PathInterpolatorCompat
import android.transition.Slide
import android.view.Gravity

class Animations {

    companion object {

        private const val duration = 400.toLong()
        private val interpolator = PathInterpolatorCompat.create(
            0.42.toFloat(),
            1.35.toFloat(),
            0.4.toFloat(),
            1.0.toFloat())

        fun getSlideLeftAnimation(): Slide {
            val slideLeft = Slide(Gravity.START)
            slideLeft.duration = duration
            slideLeft.interpolator = interpolator
            return slideLeft
        }

        fun getSlideRightAnimation(): Slide {
            val slideRight = Slide(Gravity.END)
            slideRight.duration = duration
            slideRight.interpolator = interpolator
            return slideRight
        }
    }
}