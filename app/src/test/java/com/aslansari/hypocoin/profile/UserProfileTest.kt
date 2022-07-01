package com.aslansari.hypocoin.profile

// TODO mock account repository
//@RunWith(MockitoJUnitRunner::class)
class UserProfileTest {

//    @Mock
//    var accountDAO: AccountDAO? = null
//
//    @Test
//    fun userLoginTest() {
//        val future = CompletableFuture<UserProfileAction>()
//        val accountRepository = AccountRepository(accountDAO = accountDAO!!, auth = FirebaseAuth.getInstance())
//        val userProfileViewModel = UserProfileViewModel(accountRepository)
//        userProfileViewModel.actionPublishSubject
//            .subscribeWith(object : DisposableObserver<UserProfileAction?>() {
//                override fun onNext(action: UserProfileAction?) {
//                    future.complete(action)
//                }
//
//                override fun onError(e: Throwable) {
//                    Assert.fail("on error")
//                }
//
//                override fun onComplete() {
//                    Assert.fail("on complete")
//                }
//            })
//        userProfileViewModel.login()
//        try {
//            Assert.assertEquals(UserProfileAction.LOGIN, future[5000, TimeUnit.SECONDS])
//        } catch (e: ExecutionException) {
//            e.printStackTrace()
//            Assert.fail(e.message)
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//            Assert.fail(e.message)
//        } catch (e: TimeoutException) {
//            e.printStackTrace()
//            Assert.fail(e.message)
//        }
//    }
}