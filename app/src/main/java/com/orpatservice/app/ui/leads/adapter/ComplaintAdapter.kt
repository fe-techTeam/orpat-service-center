package com.orpatservice.app.ui.leads.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.orpatservice.app.R
import com.orpatservice.app.data.model.requests_leads.Enquiry
import com.orpatservice.app.data.sharedprefs.SharedPrefs
import com.orpatservice.app.databinding.ItemComplaintBinding
import com.orpatservice.app.utils.CommonUtils
import com.orpatservice.app.utils.Constants

/**
 * Created by Ajay Yadav on 22/12/21.
 */
class ComplaintAdapter constructor(
    private val enquiryArrayList: ArrayList<Enquiry>,
    private val itemClickListener: (Int, View) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemComplaintBinding =
            ItemComplaintBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ComplaintViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ComplaintViewHolder -> {
                holder.onBind(enquiryArrayList[position], itemClickListener)
            }
        }
    }

    override fun getItemCount(): Int {
        return enquiryArrayList.size
    }

    class ComplaintViewHolder(private val binding: ItemComplaintBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(enquiry: Enquiry, itemClickListener: (Int, View) -> Unit) {

            binding.tvModelNameValue.text = enquiry.model_no
            binding.tvWarrantyStatusValue.text = enquiry.in_warranty
            binding.tvDescriptionValue.text = enquiry.nature_pf_complain

            enquiry.purchase_at?.let { binding.tvPurchaseAt.text = CommonUtils.dateFormat(it) }

            Glide.with(binding.ivInvoiceImage.context)
                .load(enquiry.invoice_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.circleCrop() // .error(R.drawable.active_dot)
                .placeholder(R.color.gray)
                .into(binding.ivInvoiceImage)

            Glide.with(binding.ivQrCodeImage.context)
                .load(enquiry.qr_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.circleCrop() // .error(R.drawable.active_dot)
                .placeholder(R.color.gray)
                .into(binding.ivQrCodeImage)

            binding.ivInvoiceImage.setOnClickListener {
                itemClickListener(
                    adapterPosition,
                    binding.ivInvoiceImage
                )
            }
            if (SharedPrefs.getInstance().getString(Constants.USER_TYPE, "")
                    .equals(Constants.SERVICE_CENTER)
            ) {
                binding.btnCloseComplaint.visibility = View.GONE

            }

            if (!enquiry.status) {
                binding.btnCloseComplaint.backgroundTintList = null

            } else {
                binding.btnCloseComplaint.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            binding.btnCloseComplaint.context,
                            R.color.gray
                        )
                    )

            }
            binding.ivQrCodeImage.setOnClickListener {
                itemClickListener(
                    adapterPosition,
                    binding.ivQrCodeImage
                )
            }
            binding.btnCloseComplaint.setOnClickListener {
                itemClickListener(
                    adapterPosition,
                    binding.btnCloseComplaint
                )
            }

        }
    }
}