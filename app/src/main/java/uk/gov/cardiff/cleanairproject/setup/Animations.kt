package uk.gov.cardiff.cleanairproject.setup

import android.support.v4.view.animation.PathInterpolatorCompat
import android.transition.Slide
import android.view.Gravity

/**
 * This class contains commonly used animations for SetupActivity fragments
 * to reduce repeating code.
 */
class Animations {

    companion object {

        //Prepare the animation duration
        private const val duration = 400.toLong()

        // Prepare a custom interpolator (like cubic-bezier in CSS)
        private val interpolator = PathInterpolatorCompat.create(
            0.3.toFloat(),
            1.25.toFloat(),
            0.5.toFloat(),
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