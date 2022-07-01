package com.orpatservice.app.ui.leads.customer_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.orpatservice.app.data.Resource
import com.orpatservice.app.data.model.SaveEnquiryResponse
import com.orpatservice.app.data.model.TechnicianResponse
import com.orpatservice.app.data.model.requests_leads.RequestLeadResponse
import com.orpatservice.app.data.remote.ErrorUtils
import com.orpatservice.app.data.repository.DataRepository
import com.orpatservice.app.data.sharedprefs.SharedPrefs
import com.orpatservice.app.ui.leads.new_lead_fragment.new_lead_request.NewRequestResponse
import com.orpatservice.app.ui.leads.new_lead_fragment.new_lead_request.UpdatePartsRequestData
import com.orpatservice.app.ui.leads.technician.TechnicianUpdateRequestResponse
import com.orpatservice.app.ui.leads.technician.ValidateProductResponse
import com.orpatservice.app.ui.leads.technician.response.TechnicianRequestLeadResponse
import com.orpatservice.app.utils.Constants
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerDetailsModel: ViewModel() {
    val invoiceUploadData = MutableLiveData<Resource<UploadFileResponse>>()
    val invoiceUpdateRequestData = MutableLiveData<Resource<UpdateRequestResponse>>()
    val assignTechnicianData = MutableLiveData<Resource<RequestLeadResponse>>()
    val qrCodeData = MutableLiveData<Resource<ValidateProductResponse>>()
    val taskCompletedData = MutableLiveData<Resource<TechnicianRequestLeadResponse>>()
    val submitLeadData = MutableLiveData<Resource<UpdateRequestResponse>>()
    val updatePartsLeadData = MutableLiveData<Resource<UpdatePartsRequestData>>()

    fun hitUploadFile(requestBody: MultipartBody) {
        return DataRepository.instance.hitAPITechnicianUploadFile(requestBody).enqueue(callbackUploadFile)
    }

    fun hitServiceCenterUploadFile(requestBody: MultipartBody) {
        return DataRepository.instance.hitAPIUploadFile(requestBody).enqueue(callbackUploadFile)
    }


    fun assignTechnicianLead() {
        DataRepository.instance.hitGetAssignTechnicianLeads().enqueue(callbackAssignTechnicianLead)
    }

    /*fun hitUpdateRequest(requestBody: JsonObject,leadId: String, taskId: String) {
       // return DataRepository.instance.hitUpdateRequestApi(requestBody).enqueue(callbackUploadFile)
        return DataRepository.instance.hitUpdateRequestApi(requestBody,leadId,taskId).enqueue(callbackUpdateRequest)
    }*/
    fun hitUpdateRequest(
        requestBody: JsonObject,
        leadId: Int?,
        taskId: Int?,
    ): LiveData<Resource<UpdateRequestResponse>> {
        return DataRepository.instance.hitUpdateRequestApi(requestBody,leadId,taskId)
    }

    /*fun hitTaskUpdateRequest(
        requestBody: JsonObject,
        leadId: Int?,
    ): LiveData<Resource<UpdateRequestResponse>> {
        return DataRepository.instance.hitTaskUpdateRequestApi(requestBody,leadId)
    }*/


    fun hitTaskUpdateRequest(requestBody: JsonObject,  leadId: Int?) {
        return DataRepository.instance.hitTaskUpdateRequestApi(requestBody,leadId).enqueue(callbackSubmitLead)
    }

    fun hitAPIServiceMarkAsCompleteLead(leadsId: Int,happyCode: String) {
        return DataRepository.instance.hitAPIUploadPartsComplete(leadsId,happyCode).enqueue(callbackUploadPartsLead)
    }

   /* fun hitAPIServiceMarkAsCompleteLead(leadsId: String,happyCode: String): LiveData<Resource<RequestLeadResponse>> {
        return DataRepository.instance.hitAPIServiceMarkAsComplete(leadsId,happyCode)
    }
*/

    private val callbackSubmitLead: Callback<UpdateRequestResponse> = object : Callback<UpdateRequestResponse> {
        override fun onResponse(
            call: Call<UpdateRequestResponse>,
            response: Response<UpdateRequestResponse>
        ) {
            if (response.isSuccessful) {
                submitLeadData.value = response.body()?.let { Resource.success(it) }
            } else {
                submitLeadData.value =
                    Resource.error(
                        ErrorUtils.getError(
                            response.errorBody(),
                            response.code()
                        )
                    )
            }
        }

        override fun onFailure(call: Call<UpdateRequestResponse>, t: Throwable) {
            submitLeadData.value = Resource.error(ErrorUtils.getError(t))
        }
    }

    private val callbackUploadPartsLead: Callback<UpdatePartsRequestData> = object : Callback<UpdatePartsRequestData> {
        override fun onResponse(
            call: Call<UpdatePartsRequestData>,
            response: Response<UpdatePartsRequestData>
        ) {
            if (response.isSuccessful) {
                updatePartsLeadData.value = response.body()?.let { Resource.success(it) }
            } else {
                updatePartsLeadData.value =
                    Resource.error(
                        ErrorUtils.getError(
                            response.errorBody(),
                            response.code()
                        )
                    )
            }
        }

        override fun onFailure(call: Call<UpdatePartsRequestData>, t: Throwable) {
            updatePartsLeadData.value = Resource.error(ErrorUtils.getError(t))
        }
    }

    fun hitTaskCompletedRequest(
        leadId: Int?,
    ): LiveData<Resource<TechnicianRequestLeadResponse>> {
        return DataRepository.instance.hitTaskCompletedRequestApi(leadId)
    }

    fun hitTechnicianUpdateRequest(
        requestBody: JsonObject,
        taskId: Int?,

    ): LiveData<Resource<TechnicianUpdateRequestResponse>> {
        return DataRepository.instance.hitTechnicianUpdateRequestApi(requestBody,taskId)
    }


    fun hitCancelRequest(
        requestBody: JsonObject,
        leadId : Int?,
    ): LiveData<Resource<CancelRequestResponse>> {
        return DataRepository.instance.hitCancelRequestApi(requestBody,leadId)
    }

   /* fun hitValidateQRApi(qrcode : String, technicianId: String){
        DataRepository.instance.hitCustomerValidateProductApi(qrcode,technicianId).enqueue(callbackQRCodeCheck)
    }*/


    fun hitValidateQRApi(
        requestBody: JsonObject,
        technicianId: String?,
    ): LiveData<Resource<UpdateRequestResponse>> {
        return DataRepository.instance.hitCustomerValidateProductApi(requestBody,technicianId)
    }


    private val callbackQRCodeCheck: Callback<ValidateProductResponse> = object : Callback<ValidateProductResponse> {
        override fun onResponse(
            call: Call<ValidateProductResponse>,
            response: Response<ValidateProductResponse>
        ) {
            if (response.isSuccessful) {
                qrCodeData.value = response.body()?.let { Resource.success(it) }
            } else {
                qrCodeData.value =
                    Resource.error(
                        ErrorUtils.getError(
                            response.errorBody(),
                            response.code()
                        )
                    )
            }
        }

        override fun onFailure(call: Call<ValidateProductResponse>, t: Throwable) {
            qrCodeData.value = Resource.error(ErrorUtils.getError(t))
        }
    }


    private val callbackUploadFile: Callback<UploadFileResponse> = object :
        Callback<UploadFileResponse> {
        override fun onResponse(
            call: Call<UploadFileResponse>,
            response: Response<UploadFileResponse>
        ) {
            if (response.isSuccessful) {
                invoiceUploadData.value = response.body()?.let { Resource.success(it) }
            } else {
                invoiceUploadData.value =
                    Resource.error(
                        ErrorUtils.getError(
                            response.errorBody(),
                            response.code()
                        )
                    )
            }
        }

        override fun onFailure(call: Call<UploadFileResponse>, t: Throwable) {
            invoiceUploadData.value = Resource.error(ErrorUtils.getError(t))
        }
    }

    private val callbackAssignTechnicianLead: Callback<RequestLeadResponse> = object :
        Callback<RequestLeadResponse> {
        override fun onResponse(
            call: Call<RequestLeadResponse>,
            response: Response<RequestLeadResponse>
        ) {
            if (response.isSuccessful) {
                assignTechnicianData.value = response.body()?.let {
                    Resource.success(it) }
                //  getUserData()
            } else {
                assignTechnicianData.value =
                    Resource.error(
                        ErrorUtils.getError(
                            response.errorBody(),
                            response.code()
                        )
                    )
            }
        }

        override fun onFailure(call: Call<RequestLeadResponse>, t: Throwable) {
            assignTechnicianData.value = Resource.error(ErrorUtils.getError(t))
        }
    }
}
