package com.orpatservice.app.ui.leads.technician.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.orpatservice.app.R
import com.orpatservice.app.databinding.ItemTechnicianComplaintBinding
import com.orpatservice.app.ui.leads.technician.response.TechnicianEnquiry
import com.orpatservice.app.ui.leads.technician.response.TechnicianEnquiryImage
import com.orpatservice.app.ui.leads.technician.response.TechnicianLeadData
import kotlin.collections.ArrayList

class TechnicianCustomerDetailsAdapter(
    private val enquiryArrayList: ArrayList<TechnicianEnquiry>,
    private val leadDataArrayList: TechnicianLeadData,
    private val itemClickListener: (Int, View, ItemTechnicianComplaintBinding, ArrayList<TechnicianEnquiryImage>) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemTechnicianComplaintBinding =
            ItemTechnicianComplaintBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ComplaintViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ComplaintViewHolder -> {
                holder.onBind(enquiryArrayList[position].lead_enquiry_images,enquiryArrayList.count(),enquiryArrayList[position],leadDataArrayList, itemClickListener)
            }
        }
    }

    override fun getItemCount(): Int {
        return enquiryArrayList.size
    }

    class ComplaintViewHolder(val binding: ItemTechnicianComplaintBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun onBind(
            enquiryImage: ArrayList<TechnicianEnquiryImage>,
            tatalCount: Int,
            enquiry: TechnicianEnquiry,
            leadData: TechnicianLeadData,
            itemClickListener: (Int, View, ItemTechnicianComplaintBinding, ArrayList<TechnicianEnquiryImage>) -> Unit
        ) {

            if(enquiry.technician_detail_status == 1){
                binding.btnTechnicianUpdate.visibility = View.GONE
                binding.btnTechnicianHideUpdate.visibility = View.VISIBLE
            }else{
                binding.btnTechnicianUpdate.visibility = View.VISIBLE
                binding.btnTechnicianHideUpdate.visibility = View.GONE
            }

            binding.tvDescriptionValue.text = enquiry.customer_discription
            binding.tvModelNameValue.text = enquiry.model_no

             enquiryImage.forEach{
                 Glide.with(binding.ivUploadImage)
                     .load(it.image)
                     // .diskCacheStrategy(DiskCacheStrategy.ALL)
                     //.circleCrop() // .error(R.drawable.active_dot)
                     .placeholder(R.color.gray)
                     .into(binding.ivUploadImage)
             }

            Glide.with(binding.ivInvoiceImage.context)
                .load(enquiry.invoice_url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.circleCrop() // .error(R.drawable.active_dot)
                .placeholder(R.color.gray)
                .into(binding.ivInvoiceImage)

            Glide.with(binding.ivQrCodeImage.context)
                .load(enquiry.dummy_barcode)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.circleCrop() // .error(R.drawable.active_dot)
                .placeholder(R.color.gray)
                .into(binding.ivQrCodeImage)


            binding.tvQrCodeNumber.text = enquiry.scanned_barcode

            binding.ivInvoiceImage.setOnClickListener {
                //binding.ivQrCodeImage.visibility = GONE
                //binding.scannerView.visibility = VISIBLE
                itemClickListener(
                    adapterPosition,
                    binding.ivInvoiceImage,binding,
                    enquiryImage
                )
            }
            binding.btnUploadImage.setOnClickListener{
                itemClickListener(
                    adapterPosition,
                    binding.btnUploadImage,binding,
                    enquiryImage
                )
            }
            binding.ivUploadImage.setOnClickListener{
                itemClickListener(
                    adapterPosition,
                    binding.ivUploadImage,binding,
                    enquiryImage
                )
            }
          /*  binding.btnUploadInvoice.setOnClickListener{
                itemClickListener(
                    adapterPosition,
                    binding.btnUploadInvoice,binding
                )
            }*/

            binding.btnTechnicianUpdate.setOnClickListener{
                    itemClickListener(
                        adapterPosition,
                        binding.btnTechnicianUpdate, binding,
                        enquiryImage

                    )
            }
            binding.tvCountImage.setOnClickListener {
                itemClickListener(
                    adapterPosition,
                    binding.tvCountImage, binding,
                    enquiryImage

                )
            }

            val posi = adapterPosition+1

            binding.tvTask.setText("Task"+" "+ posi+"/"+tatalCount)

            binding.ivQrCodeImage.setOnClickListener {
                itemClickListener(
                    adapterPosition,
                    binding.ivQrCodeImage,binding,
                    enquiryImage
                )
            }
            binding.btnScanQr.setOnClickListener {
                itemClickListener(
                    adapterPosition,
                    binding.btnScanQr,binding,
                    enquiryImage
                )
            }
        }
    }
}