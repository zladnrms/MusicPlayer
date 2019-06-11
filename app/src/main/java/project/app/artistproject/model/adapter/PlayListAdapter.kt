package project.app.artistproject.model.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.item_play_list.view.*
import project.app.artistproject.R
import project.app.artistproject.model.data.SongModel
import com.facebook.imagepipeline.core.ImagePipelineConfig
import kotlin.collections.ArrayList


class PlayListAdapter(context: Context) : RecyclerView.Adapter<PlayListAdapter.ViewHolder>() {

    private val context = context
    private var list = mutableListOf<SongModel>()
    var selectItem = MutableLiveData<SongModel>()

    init {
        val imagePipelineConfig = ImagePipelineConfig.newBuilder(context)
            .setDownsampleEnabled(true)
            .build()
        Fresco.initialize(context, imagePipelineConfig)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_play_list, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: SongModel) {
            itemView.setOnClickListener {
                selectItem.value = data
            }
            itemView.tv_artist.text = data.artist
            itemView.tv_album.text = data.album
            itemView.tv_title.text = data.title

            data.image?.let {
                val imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(data.image))
                    .setResizeOptions(ResizeOptions.forDimensions(80, 80))
                    .setProgressiveRenderingEnabled(true)
                    .build()

                itemView.iv_album.setImageRequest(imageRequest)
            }

        }
    }

    fun setList(list: ArrayList<SongModel>) {
        this.list = list
    }
}