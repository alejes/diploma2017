require 'java'
# [TODO] make path
require 'C:\Users\Alexey\Desktop\Kotlin\1_prepare\JRubyResolvingTests\out\artifacts\javaTestCore_jar\javaTestCore.jar'

module JavaTestCoreUtils
  include_package "resolving.tests"
end

def assertEquals(value, expected)
  was = false
  if expected.kind_of?(Array);
    for current in expected;
      if current == value;
        was = true
        break
      end
    end
  else
    if expected == value;
      was = true
    end
  end
  unless was;
    raise "This is wrong, actual = #{value}" unless expected == value
  end
end

class TestRunner
end


br = JavaTestCoreUtils::BasicRouter.new

x = 1
puts "Обычное разрешение"
assertEquals(br.method_(x), [2, 3])
# 36: warning: ambiguous Java methods found, using method_(int/short)
assertEquals(br.method_("one"), 0)
assertEquals(br.method_(29), [2, 3])
assertEquals(br.method_(Integer 29), [2, 3])
assertEquals(br.method_(2.9), [2, 3, 4]) #41: warning: ambiguous Java methods found, using method_(short)
#; WAT????? double!!!!


puts "Integer"
assertEquals(br.method_IntDyn(x), 0)

puts ("Supplier")

usualSupplier = JavaTestCoreUtils::Supplier.new
assertEquals(br.method_(usualSupplier.getInt), [2, 3])
assertEquals(br.method_((Integer usualSupplier.getInt)), [2, 3])
assertEquals(br.method_(usualSupplier.getString), 0)
assertEquals(br.method_(usualSupplier.getInteger), [2, 3])
assertEquals(br.method_(usualSupplier.getDouble), [2, 3, 4])

puts ("Supplier with trait")
traitSupplier = JavaTestCoreUtils::DefaultInterface_Trait_Supplier.new
assertEquals(br.method_(traitSupplier.getInt), [2, 3]) #
assertEquals(br.method_((Integer traitSupplier.getInt)), [2, 3]) #
assertEquals(br.method_(traitSupplier.getString), 0)
assertEquals(br.method_(traitSupplier.getInteger), [2, 3]) #
assertEquals(br.method_(traitSupplier.getDouble), [2, 3, 4]) #

puts ("Inheritance without integer")
assertEquals(br.methodNoInteger__(traitSupplier.getBase, traitSupplier.getInt), 0)
assertEquals(br.methodNoInteger__(traitSupplier.getBase, traitSupplier.getInteger), 0)
assertEquals(br.methodNoInteger__(traitSupplier.getDerived, traitSupplier.getInteger), 3) #
assertEquals(br.methodNoInteger__(traitSupplier.getDerived2lvl, traitSupplier.getInteger), 5)

puts ("Inheritance with integer")
assertEquals(br.method__(traitSupplier.getBase, traitSupplier.getInt), 0)
assertEquals(br.method__(traitSupplier.getBase, traitSupplier.getInteger), 0)
assertEquals(br.method__(traitSupplier.getDerived, traitSupplier.getInteger), 3) #

puts ("Supplier with Null")
assertEquals(br.method_(traitSupplier.getNull), [0, 4, 6]) ##77: warning: ambiguous Java methods found, using method_(java.lang.String)
assertEquals(br.method_(traitSupplier.getDerivedNull), [0, 4, 6]) #
assertEquals(br.method_(traitSupplier.getDerivedTypedNull), [0, 4, 6]) #

puts ("Ambiguous Interface Router")
ar = JavaTestCoreUtils::AmbiguousRouter.new
is = JavaTestCoreUtils::InterfaceSupplier.new
assertEquals(ar.method_IP12(is.getInterfaceProvider), 1)
assertEquals(ar.method_I12(is.getInterfaceProvider), 3)##85: warning: ambiguous Java methods found, using method_I12(resolving.tests.I2)

puts ("No Default Router ON JVM")