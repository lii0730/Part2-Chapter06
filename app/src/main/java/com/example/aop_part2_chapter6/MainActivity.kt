package com.example.aop_part2_chapter6

import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val minTextView: TextView by lazy {
        findViewById(R.id.minTextView)
    }
    private val secTextView: TextView by lazy {
        findViewById(R.id.secTextView)
    }
    private val timeBar: SeekBar by lazy {
        findViewById(R.id.timeBar)
    }

    private var timer: CountDownTimer? = null

    //TODO: Sound 관련 변수 참조 선언
    private lateinit var soundpool: SoundPool
    private var tickingSoundID: Int? = null
    private var finishAlarmID: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //todo: SoundPool 생성
        createSoundPool()

        //todo: SoundPool에 들어갈 Sound load
        initSoundID()

        //todo: SeekBar 상태 체크
        changeStateTimeBar()

    }

    override fun onResume() {
        super.onResume()
        soundpool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundpool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //todo: Soundpool 해제
        soundpool.release()

        //todo: 타이머 해제
        timer?.cancel()
    }

    private fun createSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundpool = SoundPool.Builder().build()
        }
    }

    private fun initSoundID() {
        tickingSoundID = soundpool.load(this, R.raw.clock_ticking_1, 1)
        finishAlarmID = soundpool.load(this, R.raw.alarm_clock_01, 1)
    }

    private fun changeStateTimeBar() {
        timeBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //TODO: 값이 변경되었을 때
                if (fromUser) updateRemainTime(progress * 60 * 1000L) //밀리초 매개변수로 전달
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //TODO: 터치 했을 때, 타이머 종료
                stopTimer()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //TODO: 터치 종료했을 때, 타이머 시작
                if (timeBar.progress == 0) {
                    stopTimer()
                } else {
                    startTimer()
                }
            }
        })
    }

    private fun startTimer() {
        timer = createTimer(timeBar.progress * 60 * 1000L)
        timer?.start()

        //TODO: Timer 시작과 동시에 ticking Sound 시작
       tickingSoundID?.let {
            soundpool.play(it, 1F, 1F, 0, -1, 1F)
        }

    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null

        soundpool.autoPause()
    }

    //TODO: 메소드 내부에 추상클래스 구현하여 return
    private fun createTimer(initialMill: Long) : CountDownTimer{

        //TODO: CountDownTimer -> abstract class, object 생성 필요
        val timer = object : CountDownTimer(initialMill, 1000L){

            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateTimeBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }
        return timer
    }

    //TODO: CountDownTimer 생성, 반환형에 직접 추상클래스 구현
//    private fun createTimer(initialMill: Long) = object : CountDownTimer(initialMill, 1000L) { // 반환형을 직접 지정해줄 수도 있음
//
//        //TODO: interval 간격마다 Tick 이벤트 발생
//        override fun onTick(millisUntilFinished: Long) {
//            updateRemainTime(millisUntilFinished)
//            updateTimeBar(millisUntilFinished)
//        }
//
//        //TODO: 타이머가 끝났을 때
//        override fun onFinish() {
//            //todo: 종료 알람 추가
//            stopTimer()
//            finishAlarmID?.let {
//                soundpool.play(it, 1F, 1F, 0, -1, 1F)
//            }
//        }
//    }

    private fun completeCountDown() {
        stopTimer()
        finishAlarmID?.let {
            soundpool.play(it,1F,1F,0,0,1F)
        }
    }

    private fun updateRemainTime(time: Long) {
        val remainSec = time / 1000 // 초로 변환

        minTextView.text = "%02d".format(remainSec / 60)
        secTextView.text = "%02d".format(remainSec % 60)
    }

    private fun updateTimeBar(time: Long) {
        //todo : Update Seekbar progress , progress = Remain Minute
        timeBar.progress = (time / 60 / 1000).toInt()
    }
}