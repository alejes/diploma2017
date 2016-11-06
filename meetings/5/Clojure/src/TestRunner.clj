(ns TestRunner
    (:import (resolving.tests BasicRouter Supplier DefaultInterface_Trait_Supplier Derived AmbiguousRouter InterfaceSupplier DefaultRouter)))

(print (.toUpperCase "fred"))

(def br (new BasicRouter))

(println "Обычное разрешение")

(def xdyn 1)
(def x (int 1))

(assert (= 4 (. br method_ x)))
(assert (= 0 (. br method_ (new String "one"))))
(assert (= 1 (. br method_ 29)))
(assert (= 4 (. br method_ (int 29))))
(assert (= 4 (. br method_ (new Integer 29))))
(assert (= 1 (. br method_ 2.9)))
(assert (= 1 (. br method_ xdyn)))

(println "Integer")

(assert (= 1 (. br method_IntDyn xdyn)))
(assert (= 1 (. br method_IntDyn 1)))
(assert (= 1 (. br method_IntDyn (int 1))))



(println "Supplier")

(def usualSupplier (new Supplier))

(assert (= 4 (. br method_ (. usualSupplier getInt))))
(assert (= 4 (. br method_ (int (. usualSupplier getInt)))))
(assert (= 0 (. br method_ (. usualSupplier getString))))
(assert (= 4 (. br method_ (. usualSupplier getInteger))))
(assert (= 1 (. br method_ (. usualSupplier getDouble))))

(println "Default Interface Suppplier")
(def traitSupplier (new DefaultInterface_Trait_Supplier))

(assert (= 4 (. br method_ (. traitSupplier getInt))))
(assert (= 4 (. br method_ (int (. traitSupplier getInt)))))
(assert (= 0 (. br method_ (. traitSupplier getString))))
(assert (= 4 (. br method_ (. traitSupplier getInteger))))
(assert (= 1 (. br method_ (. traitSupplier getDouble))))


(println "Inheritance without integer")
(assert (= 0 (. br methodNoInteger__ (. traitSupplier getBase) (. traitSupplier getInt))))
(assert (= 0 (. br methodNoInteger__ (. traitSupplier getBase) (. traitSupplier getInteger))))
(assert (= 1 (. br methodNoInteger__ (. traitSupplier getDerived) (. traitSupplier getInteger))))
(assert (= 5 (. br methodNoInteger__ (. traitSupplier getDerived2lvl) (. traitSupplier getInteger))))


(println "Inheritance with integer")
(assert (= 0 (. br method__ (. traitSupplier getBase) (. traitSupplier getInt))))
(assert (= 0 (. br method__ (. traitSupplier getBase) (. traitSupplier getInteger))))
(assert (= 4 (. br method__ (. traitSupplier getDerived) (. traitSupplier getInteger))))


(println "Supplier with Null")

(println (. br method_ (. traitSupplier getNull)))
(println (. br method_ (. traitSupplier getDerivedNull)))
(println (. br method_ (cast Derived (. traitSupplier getDerivedNull))))
(println (. br method_ (. traitSupplier getDerivedTypedNull)))

(println "6 or 0 or 4")
; (assert (= 4 (. br method_ (. traitSupplier getNull))))
; (assert (= 4 (. br method_ (. traitSupplier getDerivedNull))))
; (assert (= 4 (. br method_ (cast Derived (. traitSupplier getDerivedNull)))))
; (assert (= 4 (. br method_ (. traitSupplier getDerivedTypedNull))))


(println "Ambiguous Interface Router")
(def ar (new AmbiguousRouter))
(def is (new InterfaceSupplier))


(assert (= 1 (. ar method_IP12 (. is getInterfaceProvider))))
(assert (= 2 (. ar method_I12 (. is getInterfaceProvider))))
; !!!

(println "Default Router")
(println "dont work with java")
(def dr (new DefaultRouter))
(assert (= 1 (. dr method__ (. traitSupplier getDerived))))
; (assert (= 2 (. dr method__ (. traitSupplier getBase))))
