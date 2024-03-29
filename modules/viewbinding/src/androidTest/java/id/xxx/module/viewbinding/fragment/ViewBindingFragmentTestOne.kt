package id.xxx.module.viewbinding.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import id.xxx.module.viewbinding.databinding.ContainerMainBinding
import org.junit.Test

class ViewBindingFragmentTestOne {

    companion object {

        const val TEXT_HEAD = "TEXT HEAD"
        const val TEXT_HEAD_CHANGE = "TEXT HEAD CHANGE"
    }

    class ExampleFragment : ViewBindingFragment<ContainerMainBinding>() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            viewBinding.tvHead.text = TEXT_HEAD
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            viewBinding.tvHead.setOnClickListener {
                viewBinding.tvHead.text = TEXT_HEAD_CHANGE
            }
        }
    }

    @Test
    fun test() {
        launchFragmentInContainer<ExampleFragment>()
        Espresso
            .onView(ViewMatchers.withText(TEXT_HEAD))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())
        Espresso
            .onView(ViewMatchers.withText(TEXT_HEAD_CHANGE))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.pressBackUnconditionally()
    }
}