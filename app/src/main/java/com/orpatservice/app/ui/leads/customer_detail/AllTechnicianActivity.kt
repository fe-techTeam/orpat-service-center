package com.orpatservice.app.ui.leads.customer_detail

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orpatservice.app.R
import com.orpatservice.app.base.Callback
import com.orpatservice.app.data.Resource
import com.orpatservice.app.data.Status
import com.orpatservice.app.data.model.TechnicianData
import com.orpatservice.app.data.model.TechnicianResponse
import com.orpatservice.app.data.model.requests_leads.RequestLeadResponse
import com.orpatservice.app.databinding.ActivityAllTechnicianBinding
import com.orpatservice.app.databinding.ActivityAssignToTechnicianBinding
import com.orpatservice.app.ui.admin.dashboard.DashboardActivity
import com.orpatservice.app.ui.admin.technician.TechniciansViewModel
import com.orpatservice.app.ui.leads.adapter.AllTechnicianAdapter
import com.orpatservice.app.ui.leads.new_lead_fragment.new_lead_request.NewRequestResponse
import com.orpatservice.app.ui.leads.new_lead_fragment.new_lead_request.RequestData
import com.orpatservice.app.ui.leads.service_center.RequestLeadActivity
import com.orpatservice.app.ui.leads.technician.TechnicianCustomerDetailsActivity
import com.orpatservice.app.ui.leads.technician.TechnicianRequestLeadActivity
import com.orpatservice.app.utils.Constants
import com.orpatservice.app.utils.DividerItemDecorator
import com.orpatservice.app.utils.Utils
import com.tapadoo.alerter.Alerter

class AllTechnicianActivity : AppCompatActivity() ,Callback{
    private lateinit var binding: ActivityAssignToTechnicianBinding
    private val techList: ArrayList<RequestData> = ArrayList()
    lateinit var viewModel: TechniciansViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var isLoading: Boolean = false
    private var pageNumber = 1
    private var totalPage = 1
    private var isNave = ""
    private var technician_user = ""
    private var selected_technicianId: String? = null
    private lateinit var alltechnicianAdapter: AllTechnicianAdapter
    //Click listener for List Item


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignToTechnicianBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[TechniciansViewModel::class.java]
        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            title = ""
            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        isNave = intent.getStringExtra(Constants.LEADS_ID).toString()
        technician_user = intent.getStringExtra(Constants.TECHNICIAN_ID).toString()

        alltechnicianAdapter = AllTechnicianAdapter(techList,itemClickListener = onItemClickListener,isNave,technician_user)

        linearLayoutManager = LinearLayoutManager(this)

        val dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.rv_divider))

        binding.rvAllTechList.apply {
            adapter = alltechnicianAdapter
            addItemDecoration(dividerItemDecoration)
            layoutManager = linearLayoutManager

        }
        alltechnicianAdapter.callback = this

        userUtilData()
        setObserver()
        addScrollerListener()

      //  viewModel.loadAssignedTechnicianLeads(pageNumber)

    }

    private fun userUtilData() {

        binding.btnAssignTechnician.setOnClickListener {

            if(selected_technicianId != null) {

                viewModel.hitAPIAssignTechnicianLead(
                    isNave,
                    techList[clickedPosition!!].id.toString()
                ).observe(this, loadAssignTechnician())
            }else{
                Utils.instance.popupPinUtil(this,
                   "Please select technician!",
                    "",
                    false)
            }
        }
    }

    private val onItemClickListener: (Int, View) -> Unit = { position, view ->
        when (view.id) {
            R.id.radio_technician -> {

                clickedPosition = position
                selected_technicianId = techList[position].id.toString()
            }
            R.id.li_technician_name -> {

                clickedPosition = position
                selected_technicianId = techList[position].id.toString()
            }
        }
    }

    private fun addScrollerListener() {
        //attaches scrollListener with RecyclerView
        binding.rvAllTechList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == techList.size - 1 && totalPage > pageNumber) {
                        pageNumber++
                        viewModel.loadAssignedTechnicianLeads(pageNumber)
                        isLoading = true
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setObserver() {
        viewModel.technicianList.observe(this, this::getAssignedLeads)
        viewModel.loadTechnicianLeads(pageNumber,isNave.toInt())
    }

    private var nextPage: String? = null

    private fun getAssignedLeads(resources: Resource<NewRequestResponse>) {
        when (resources.status) {
            Status.LOADING -> {
                binding.cpiLoading.visibility = View.VISIBLE

            }
            Status.ERROR -> {
                binding.cpiLoading.visibility = View.GONE
               /* Utils.instance.popupPinUtil(this,
                    resources.error?.message.toString(),
                    "",
                    false)*/
            }
            else -> {
                binding.cpiLoading.visibility = View.GONE

                val data = resources.data

                data.let {
                    if(it?.success == true){
                       // totalPage = it.data.pagination.last_page
                        techList.addAll(it.data.data)
                       // nextPage = it.data.pagination.next_page_url

                        //technicianAdapter.notifyDataSetChanged()
                        isLoading = false

                        if (pageNumber == 1)
                            alltechnicianAdapter.notifyDataSetChanged()
                        else
                            alltechnicianAdapter.notifyItemInserted(techList.size - 1)

                    }else{
                        it?.message?.let { msg ->
                            // Utils.instance.popupUtil(this@CustomerDetailsActivity, msg, null, false)
                        }
                        val r = Runnable {
                            // barcodeView?.resume()
                        }
                        Handler().postDelayed(r, 1000)
                    }
                }.run {  }
            }
        }
    }

    var clickedPosition: Int? = null
    override fun onItemClick(view: View, position: Int) {
        when (view.id) {
           /* R.id.btn_assign_alltechnician -> {
                clickedPosition = position

                viewModel.hitAPIAssignTechnicianLead(isNave, techList[position].id.toString()).observe(this, loadAssignTechnician())
            }*/
            R.id.radio_technician ->{
               /* Toast.makeText(getApplicationContext(),
                    "Group Clicked ",
                     Toast.LENGTH_SHORT).show();*/
            }
        }
    }

    private fun loadAssignTechnician(): Observer<Resource<TechnicianResponse>> {
        return Observer { it ->
            when (it?.status) {
                Status.LOADING -> {
                    binding.cpiLoading.visibility = View.VISIBLE

                }
                Status.ERROR -> {
                    isLoading = false
                    binding.cpiLoading.visibility = View.GONE

                      /*  Utils.instance.popupPinUtil(this,
                            it.error?.message.toString(),
                            "",
                            false)*/


                }
                else -> {
                    binding.cpiLoading.visibility = View.GONE
                    val data = it?.data

                    data?.let {
                        if (it.success) {
                            //confirmationDialog(it.message)
                           /* Alerter.create(this@AllTechnicianActivity)
                                .setTitle("")
                                .setText(data.message.toString())
                                .setBackgroundColorRes(R.color.orange)
                                .setDuration(1000)
                                .show()*/

                            it.message?.toString()?.let { it1 ->
                                Utils.instance.popupPinUtil(this,
                                    it1,
                                    "",
                                    true)
                            }
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(this, RequestLeadActivity::class.java)
                                startActivity(intent)
                                finish()

                            }, 5000)
                        }
                    } ?: run {
                    }
                }
            }
        }
    }
}
