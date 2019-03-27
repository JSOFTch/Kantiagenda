package com.simplemobiletools.studentcalendarpaid.activities

import androidx.fragment.app.FragmentActivity
import android.os.Bundle
import android.content.Intent
import com.simplemobiletools.studentcalendarpaid.BuildConfig
import org.sufficientlysecure.donations.DonationsFragment
import com.simplemobiletools.studentcalendarpaid.R



class DonationsActivity: FragmentActivity() {


    private val GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzwIqxj2pHF9kmXL4m1HOEdP2CX79OOO3N6dRZxiR58+raOw6XStv7IbWDE+OpUqaE2oCQHeJ9spMhgXdo6LZairV8n1B5T5R4wEIhDLQ5nBfT3ZXqcuD+0tgNGglf/mX5I6L1qePF0QAAbcEApIJIUBjNNC3MFNryL8ptwgnAvwDKb9qCWh9/evtzwqedy+8HPVt3CZXQxAlDA4usoBRkrfPV6/pdRAlsxQppZ4rzt9lxm153n0DU8JyppcmTfv50tS3YqPblW4lBP5ajBnNAKsS6L8NBDtLlFjKUqlPH9CXPGdMnVU62CTtDHQbbK2ZcxzBoWnvwjNa3f6yO2Xw6QIDAQAB"
    private val GOOGLE_CATALOG = arrayOf("donations1")


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_donations)

        val ft = supportFragmentManager.beginTransaction()
        val donationsFragment: DonationsFragment
        donationsFragment = DonationsFragment.newInstance(BuildConfig.DEBUG, true, GOOGLE_PUBKEY, GOOGLE_CATALOG,
                resources.getStringArray(R.array.donation_google_catalog_values), false, null, null, null, false, null, null, false, null)

        ft.replace(R.id.donations_activity_container, donationsFragment, "donationsFragment")
        ft.commit()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag("donationsFragment")
        fragment?.onActivityResult(requestCode, resultCode, data)
    }


}
