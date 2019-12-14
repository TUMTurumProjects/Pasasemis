class w08h02 {
  import org.junit.Test
  import org.junit.Assert._
  import java.util._
  import master.pgdp.{iter => master}
  import testee.pgdp.{iter => test}

  @Test
  def test0() {
    val range_m : master.Range = new master.Range(14, 24, 3)
    val range_t : test.Range = new test.Range(14, 24, 3)

    val iter_m : Iterator[Integer] = range_m.iterator()
    val iter_t : Iterator[Integer] = range_t.iterator()
    
    while (iter_m.hasNext()) {
      val a : Integer = iter_m.next()
      val b : Integer = iter_t.next()
      val msg : String = "should be " + a + ", but was " + b
      assertEquals(msg,a,b)
    }

  }
  
  @Test
  def test1() {
    val range_m : master.Range = new master.Range(3, 24, 3)
    val range_t : test.Range = new test.Range(3, 24, 3)

    val iter_m : Iterator[Integer] = range_m.iterator()
    val iter_t : Iterator[Integer] = range_t.iterator()

    while (iter_m.hasNext()) {
      val a : Integer = iter_m.next()
      val b : Integer = iter_t.next()
      val msg : String = "should be " + a + ", but was " + b
      assertEquals(msg,a,b)
    }

  }

  @Test
  def test2() {
    val range_m : master.Range = new master.Range(3, -200, 3)
    val range_t : test.Range = new test.Range(3, -200, 3)

    val iter_m : Iterator[Integer] = range_m.iterator()
    val iter_t : Iterator[Integer] = range_t.iterator()
    
    while (iter_m.hasNext()) {
      val a : Integer = iter_m.next()
      val b : Integer = iter_t.next()
      val msg : String = "should be " + a + ", but was " + b
      assertEquals(msg,a,b)
    }

  }

  @Test
  def test3() {
    val range_m : master.Range = new master.Range(-3, 200, 3)
    val range_t : test.Range = new test.Range(-3, 200, 3)

    val iter_m : Iterator[Integer] = range_m.iterator()
    val iter_t : Iterator[Integer] = range_t.iterator()

    while (iter_m.hasNext()) {
      val a : Integer = iter_m.next()
      val b : Integer = iter_t.next()
      val msg : String = "should be " + a + ", but was " + b
      assertEquals(msg,a,b)
    }

  }

  @Test
  def test4() {
    val range_m : master.Range = new master.Range(-500, 5, 1)
    val range_t : test.Range = new test.Range(-500, 5, 1)

    val iter_m : Iterator[Integer] = range_m.iterator()
    val iter_t : Iterator[Integer] = range_t.iterator()

    while (iter_m.hasNext()) {
      val a : Integer = iter_m.next()
      val b : Integer = iter_t.next()
      val msg : String = "should be " + a + ", but was " + b
      assertEquals(msg,a,b)
    }


  }

  @Test
  def test5() {
    val range_m : master.Range = new master.Range(-10768,10e5.toInt, 3)
    val range_t : test.Range = new test.Range(-10768, 10e5.toInt, 3)

    val iter_m : Iterator[Integer] = range_m.iterator()
    val iter_t : Iterator[Integer] = range_t.iterator()

    while (iter_m.hasNext()) {
      val a : Integer = iter_m.next()
      val b : Integer = iter_t.next()
      val msg : String = "should be " + a + ", but was " + b
      assertEquals(msg,a,b)
    }

  }

  @Test
  def test6() {
    val piM : master.PasswordIterator = new master.PasswordIterator(1)
    val piT : test.PasswordIterator = new test.PasswordIterator(1)
    while (piM.hasNext()) {
      assertEquals(piM.next(), piT.next())
    }

  }

  @Test
  def test7() {
    val piM : master.PasswordIterator = new master.PasswordIterator(6)
    val piT : test.PasswordIterator = new test.PasswordIterator(6)
    while (piM.hasNext()) {
      assertEquals(piM.next(), piT.next())
    }

  }


  @Test
  def tesе8() {
    val piM : master.PasswordIterator = new master.PasswordIterator(2)
    val piT : test.PasswordIterator = new test.PasswordIterator(2)
    while (piM.hasNext()) {
      assertEquals(piM.next(), piT.next())
    }

  }

    @Test
  def test9() {
    val piM : master.PasswordIterator = new master.PasswordIterator(3)
    val piT : test.PasswordIterator = new test.PasswordIterator(3)
    while (piM.hasNext()) {
      assertEquals(piM.next(), piT.next())
    }

  }

    @Test
  def test10() {
    val piM : master.PasswordIterator = new master.PasswordIterator(4)
    val piT : test.PasswordIterator = new test.PasswordIterator(4)
    while (piM.hasNext()) {
      assertEquals(piM.next(), piT.next())
    }

  }

  @Test
  def test11() {
    val piM : master.PasswordIterator = new master.PasswordIterator(5)
    val piT : test.PasswordIterator = new test.PasswordIterator(5)
    while (piM.hasNext()) {
      assertEquals(piM.next(), piT.next())
    }

  }


  @Test
  def test12() {
    val salt : String = "QSZT"
    val hash : Int = -755768890
    val length : Int = 5
    val msg : String = "failed with salt: " + salt + ", hash: " + hash + ", length" + length
    assertEquals(msg,master.PasswordBreaker.findPassword((s) => s.hashCode, length, salt, hash), 
                  test.PasswordBreaker.findPassword((s) => s.hashCode, length, salt, hash))
  }

  @Test
  def test13() {
    val salt : String = "PASASEMIS"
    val hash : Int = -303230991
    val length : Int = 5
    assertEquals(master.PasswordBreaker.findPassword((s) => s.hashCode, length, salt, hash), 
                  test.PasswordBreaker.findPassword((s) => s.hashCode, length, salt, hash))
  }

  @Test
  def test14() {
    val salt : String = "puten"
    val hash : Int = 226402886
    val length : Int = 3
    assertEquals(master.PasswordBreaker.findPassword((s) => s.hashCode, length, salt, hash), 
                  test.PasswordBreaker.findPassword((s) => s.hashCode, length, salt, hash))
  }

  @Test
  def test15() {
    val salt : String = "ya lubly vashich mam"
    val hash : Int = 19027887
    val length : Int = 6
    assertEquals(master.PasswordBreaker.findPassword((s) => s.hashCode, length, salt, hash), 
                  test.PasswordBreaker.findPassword((s) => s.hashCode, length, salt, hash))
  }
}

