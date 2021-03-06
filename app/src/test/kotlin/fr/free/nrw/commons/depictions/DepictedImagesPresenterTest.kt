package fr.free.nrw.commons.depictions

import fr.free.nrw.commons.Media
import fr.free.nrw.commons.depictions.Media.DepictedImagesFragment
import fr.free.nrw.commons.depictions.Media.DepictedImagesPresenter
import fr.free.nrw.commons.explore.depictions.DepictsClient
import fr.free.nrw.commons.kvstore.JsonKvStore
import fr.free.nrw.commons.media.MediaClient
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class DepictedImagesPresenterTest {

    @Mock
    internal lateinit var view: DepictedImagesFragment

    lateinit var depictedImagesPresenter: DepictedImagesPresenter

    @Mock
    lateinit var jsonKvStore: JsonKvStore

    @Mock
    lateinit var depictsClient: DepictsClient

    @Mock
    lateinit var mediaClient: MediaClient

    lateinit var testScheduler: TestScheduler

    val mediaList: ArrayList<Media> = ArrayList()

    @Mock
    lateinit var mediaItem: Media

    var testObservable: Observable<List<Media>>? = null


    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        testScheduler = TestScheduler()
        mediaList.add(mediaItem)
        testObservable = Observable.just(mediaList)
        depictedImagesPresenter = DepictedImagesPresenter(jsonKvStore, depictsClient, mediaClient, testScheduler, testScheduler)
        depictedImagesPresenter.onAttachView(view)
    }

    @Test
    fun initList() {
        Mockito.`when`(
            depictsClient.fetchImagesForDepictedItem(ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt())
        ).thenReturn(testObservable)
        depictedImagesPresenter.initList("rabbit")
        depictedImagesPresenter.handleSuccess(mediaList)
        verify(view)?.handleSuccess(mediaList)
    }

    @Test
    fun replaceTitlesWithCaptions() {
        var stringObservable: Single<String>? = Single.just(String())
        Mockito.`when`(mediaClient.getCaptionByWikibaseIdentifier(ArgumentMatchers.anyString()))?.thenReturn(stringObservable)
        depictedImagesPresenter.replaceTitlesWithCaptions("File:rabbit.jpg", 0)
        testScheduler.triggerActions()
        verify(view)?.handleLabelforImage("", 0)
    }
}
