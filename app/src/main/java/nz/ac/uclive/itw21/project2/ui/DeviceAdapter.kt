package nz.ac.uclive.itw21.project2.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import nz.ac.uclive.itw21.project2.R
import nz.ac.uclive.itw21.project2.database.Device
import nz.ac.uclive.itw21.project2.database.DeviceViewModel
import nz.ac.uclive.itw21.project2.helper.FullscreenImageActivity
import java.time.Period
import java.time.ZoneId
import java.util.*


class DeviceAdapter internal constructor(context: Context) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var deviceList = emptyList<Device>()
    private lateinit var itemView: View
    private lateinit var deviceViewModel: DeviceViewModel


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        itemView = inflater.inflate(R.layout.fragment_device, parent, false)
        deviceViewModel = ViewModelProvider((itemView.context as FragmentActivity)).get(DeviceViewModel::class.java)
        return DeviceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val currentItem = deviceList[position]
        holder.deviceName.text = currentItem.deviceName
        holder.deviceWarrantyPeriod.text = calculateWarrantyPeriodRemaining(currentItem.warrantyEndDate)
        holder.deviceTypeText.text = currentItem.type
        holder.devicePrice.text = currentItem.price
        holder.devicePurchaseDate.text = itemView.context.getString(R.string.format_purchase_date, currentItem.dateOfPurchase)
        holder.deviceVendor.text = currentItem.vendor

        if (currentItem.deviceImageUri.isNotEmpty()) {
            holder.deviceImage.foreground = null

            holder.deviceImage.setImageURI(Uri.parse(currentItem.deviceImageUri))

            holder.deviceImage.setOnClickListener{
                val fullScreenIntent = Intent(holder.view.context, FullscreenImageActivity::class.java)
                fullScreenIntent.putExtra("URI", currentItem.deviceImageUri)
                holder.view.context.startActivity(fullScreenIntent)
            }
        }

        if (currentItem.receiptImageUri.isNotEmpty()) {
            holder.deviceReceipt.foreground = null
            holder.deviceReceipt.setImageURI(Uri.parse(currentItem.receiptImageUri))

            holder.deviceReceipt.setOnClickListener{
                val fullScreenIntent = Intent(holder.view.context, FullscreenImageActivity::class.java)
                fullScreenIntent.putExtra("URI", currentItem.receiptImageUri)
                holder.view.context.startActivity(fullScreenIntent)
            }
        }

        handleDeviceIcon(holder, currentItem)

        // Used to handle viewing more details.
        holder.view.setOnClickListener {
            if (holder.moreDetails.visibility == View.GONE) {
                holder.moreDetails.visibility = View.VISIBLE
                holder.deleteDevice.visibility = View.VISIBLE
                holder.expandArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
            } else {
                holder.moreDetails.visibility = View.GONE
                holder.deleteDevice.visibility = View.GONE
                holder.expandArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
            }
        }

        holder.deleteDevice.setOnClickListener {
            MaterialAlertDialogBuilder(itemView.context, R.style.ThemeOverlay_AppCompat_Dialog_Alert)
                .setTitle(itemView.context.resources.getString(R.string.header_delete))
                .setMessage(itemView.context.resources.getString(R.string.prompt_delete_device))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    deviceViewModel.deleteDevice(currentItem)
                }
                .setNegativeButton(android.R.string.no, null).show()

        }
    }

    private fun handleDeviceIcon(holder: DeviceViewHolder, currentItem: Device) {
        when(currentItem.type) {
            "Headphones" -> holder.deviceTypeIcon.setImageDrawable(itemView.resources.getDrawable(R.drawable.device_headphones, itemView.context.applicationContext.theme))
            "Kitchenware" -> holder.deviceTypeIcon.setImageDrawable(itemView.resources.getDrawable(R.drawable.device_kitchen, itemView.context.applicationContext.theme))
            "Laptop" -> holder.deviceTypeIcon.setImageDrawable(itemView.resources.getDrawable(R.drawable.device_laptop, itemView.context.applicationContext.theme))
            "Mouse" -> holder.deviceTypeIcon.setImageDrawable(itemView.resources.getDrawable(R.drawable.device_mouse, itemView.context.applicationContext.theme))
            "PC" -> holder.deviceTypeIcon.setImageDrawable(itemView.resources.getDrawable(R.drawable.device_pc, itemView.context.applicationContext.theme))
            "Phone" -> holder.deviceTypeIcon.setImageDrawable(itemView.resources.getDrawable(R.drawable.device_smartphone, itemView.context.applicationContext.theme))
            "Tablet" -> holder.deviceTypeIcon.setImageDrawable(itemView.resources.getDrawable(R.drawable.device_tablet, itemView.context.applicationContext.theme))
            "TV" -> holder.deviceTypeIcon.setImageDrawable(itemView.resources.getDrawable(R.drawable.device_tv, itemView.context.applicationContext.theme))
            "Watch" -> holder.deviceTypeIcon.setImageDrawable(itemView.resources.getDrawable(R.drawable.device_watch, itemView.context.applicationContext.theme))
            else -> holder.deviceTypeIcon.setImageDrawable(itemView.resources.getDrawable(R.drawable.device_other, itemView.context.applicationContext.theme))
        }
    }


    private fun calculateWarrantyPeriodRemaining(warrantyEndDate: Date): String {
        val period = Period.between(
            Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
            warrantyEndDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        )


        if (period.years > 0) {
            return "${period.years} y."
        }
        if (period.days < 0) {
            return "Past"
        }
        if (period.days == 0) {
            return "Today!"
        }

        return "${period.days} d."
    }

    internal fun setDeviceList(deviceList: List<Device>) {
        this.deviceList = deviceList
        notifyDataSetChanged()
    }


    override fun getItemCount() = deviceList.size

    /**
     * A view holder represents a single row in our list, one instance of it contains one instance
     * of a Device. This caches these views, so they can be accessed quickly rather than
     * every time the screen scrolls.
     */
    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view: View = itemView
        val deviceTypeIcon: ImageView = itemView.findViewById(R.id.device_icon)
        val deviceName: TextView = itemView.findViewById(R.id.device_name)
        val deviceWarrantyPeriod: TextView = itemView.findViewById(R.id.device_warranty_period)
        val devicePrice: TextView = itemView.findViewById(R.id.device_price)
        val deviceTypeText: TextView = itemView.findViewById(R.id.device_type)
        val devicePurchaseDate: TextView = itemView.findViewById(R.id.device_date_of_purchase)
        val deviceVendor: TextView = itemView.findViewById(R.id.device_vendor)
        val deviceImage: ImageView = itemView.findViewById(R.id.device_image)
        val deviceReceipt: ImageView = itemView.findViewById(R.id.device_receipt)
        val moreDetails: ConstraintLayout = itemView.findViewById(R.id.detailed_view)
        val expandArrow: ImageView = itemView.findViewById(R.id.expand_card)
        val deleteDevice: ImageView = itemView.findViewById(R.id.delete_device)
    }
}