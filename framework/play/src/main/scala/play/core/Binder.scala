package play.core

import scala.annotation._

@implicitNotFound("No queryString binder found for type ${A}. Try to implement an implicit QueryStringBindable for this type.")
trait QueryStringBindable[A] {
    def bind(key:String, params:Map[String,Seq[String]]):Option[Either[String,A]]
    def unbind(key:String, value:A):String
}

@implicitNotFound("No URL path binder found for type ${A}. Try to implement an implicit PathBindable for this type.")
trait PathBindable[A] {
    def bind(key:String, value:String):Either[String,A]
    def unbind(key:String, value:A):String
}

object QueryStringBindable {

    implicit def bindableString = new QueryStringBindable[String] {
        def bind(key:String, params:Map[String,Seq[String]]) = params.get(key).flatMap(_.headOption).map(Right(_))
        def unbind(key:String, value:String) = key + "=" + value
    }

    implicit def bindableInt = new QueryStringBindable[Int] {
        def bind(key:String, params:Map[String,Seq[String]]) = params.get(key).flatMap(_.headOption).map { i =>
            try {
                Right(Integer.parseInt(i))
            } catch {
                case e:NumberFormatException => Left("Cannot parse parameter " + key + " as Int: " + e.getMessage)
            }
        }
        def unbind(key:String, value:Int) = key + "=" + value.toString
    }
    
    implicit def bindableLong = new QueryStringBindable[Long] {
        def bind(key:String, params:Map[String,Seq[String]]) = params.get(key).flatMap(_.headOption).map { i =>
            try {
                Right(java.lang.Long.parseLong(i))
            } catch {
                case e:NumberFormatException => Left("Cannot parse parameter " + key + " as Long: " + e.getMessage)
            }
        }
        def unbind(key:String, value:Long) = key + "=" + value.toString
    }
    
    implicit def bindableInteger = new QueryStringBindable[Integer] {
        def bind(key:String, params:Map[String,Seq[String]]) = params.get(key).flatMap(_.headOption).map { i =>
            try {
                Right(Integer.parseInt(i))
            } catch {
                case e:NumberFormatException => Left("Cannot parse parameter " + key + " as Integer: " + e.getMessage)
            }
        }
        def unbind(key:String, value:Integer) = key + "=" + value.toString
    }
    
    implicit def bindableOption[T : QueryStringBindable] = new QueryStringBindable[Option[T]] {
      def bind(key:String, params:Map[String,Seq[String]]) = 
          Some( implicitly[QueryStringBindable[T]]
                    .bind(key, params)
                    .map(_.right.map(Some(_)))
                    .getOrElse(Right(None)) )
      def unbind(key:String, value:Option[T]) = value.map(implicitly[QueryStringBindable[T]].unbind(key, _)).getOrElse("")
    }

}


object PathBindable {
    
    implicit def bindableString = new PathBindable[String] {
        def bind(key:String, value:String) = Right(value)
        def unbind(key:String, value:String) = value
    }
    
    implicit def bindableInt = new PathBindable[Int] {
        def bind(key:String, value:String) = {
            try {
                Right(Integer.parseInt(value))
            } catch {
                case e:NumberFormatException => Left("Cannot parse parameter " + key + " as Int: " + e.getMessage)
            }
        }
        def unbind(key:String, value:Int) = value.toString
    }
    
    implicit def bindableLong = new PathBindable[Long] {
        def bind(key:String, value:String) = {
            try {
                Right(java.lang.Long.parseLong(value))
            } catch {
                case e:NumberFormatException => Left("Cannot parse parameter " + key + " as Long: " + e.getMessage)
            }
        }
        def unbind(key:String, value:Long) = value.toString
    }
    
    implicit def bindableInteger = new PathBindable[Integer] {
        def bind(key:String, value:String) = {
            try {
                Right(Integer.parseInt(value))
            } catch {
                case e:NumberFormatException => Left("Cannot parse parameter " + key + " as Integer: " + e.getMessage)
            }
        }
        def unbind(key:String, value:Integer) = value.toString
    }
    
}
