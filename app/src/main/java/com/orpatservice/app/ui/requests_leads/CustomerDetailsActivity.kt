package com.orpatservice.app.ui.requests_leads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.orpatservice.app.R
import com.orpatservice.app.ui.data.model.requests_leads.LeadData
import com.orpatservice.app.utils.Constants

class CustomerDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_details)

        val intent: Intent = getIntent()
        val leadData = intent.getSerializableExtra(Constants.LEAD_DATA) as LeadData

        Toast.makeText(this,"data: " + leadData.id, Toast.LENGTH_SHORT).show()
    }
}