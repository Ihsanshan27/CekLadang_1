package com.dicoding.cekladang.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns.EMAIL_ADDRESS
import androidx.core.content.ContextCompat
import com.dicoding.cekladang.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EmailEdText @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null,
) : TextInputEditText(context, attributeSet) {
    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { input ->
                    val getHelper = parent.parent as TextInputLayout
                    if (EMAIL_ADDRESS.matcher(input).matches()) {
                        getHelper.helperText = null
                    } else {
                        setError(context.getString(R.string.email_warning), null)
                        getHelper.helperText = context.getString(R.string.email_warning)
                        getHelper.setHelperTextColor(
                            ContextCompat.getColorStateList(context, R.color.red)
                        )
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}