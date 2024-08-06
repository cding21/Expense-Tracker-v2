package au.com.cding21.third_party.banks

import au.com.cding21.third_party.banks.allocators.SynchronousAllocator
import au.com.cding21.third_party.banks.types.Account
import au.com.cding21.third_party.banks.types.BankTransaction
import au.com.cding21.third_party.banks.util.*
import com.microsoft.playwright.BrowserContext
import com.microsoft.playwright.Page
import com.microsoft.playwright.TimeoutError
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.*
import java.rmi.ServerException
import java.util.*
import javax.security.auth.login.CredentialException

/**
 * Stateless ING API client to access ING account data
 */
class INGClientImpl(
    private val allocator: SynchronousAllocator,
    private val username: String,
    private val password: List<Int>,
) : BankClient {
    companion object {
        val IMAGE_REFERENCES: List<ImageCodec> =
            listOf(
                ImageCodec.fromBase64(
                    "iVBORw0KGgoAAAANSUhEUgAAALQAAABuCAYAAACOaDl7AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAPhSURBVHhe7dyxShxRGIbhcQu9CPtcSO5BSLUiSJqgadIGvICkT5EuXSB10qYIFhYqpBJsA4rEQiHaeDLfOhPC5J/ddfasmfl4f3jQXc9u9XI4O7trUU/aKFbPN0e75+PRfvnzqpSAHruqWt1Vu1XG93MxLtbLPx42HgAMQ9muGp7EPNmZq5gvt0bpZnsl3T0vUgJ6TI2qVTVbRX10slOsFWebo506ZkLG0KjZOmq1XFTnkEnt0QOAvlO71S69rx36VjfYnTFUarcK+qaY/FKKFgJDUXdM0LBA0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0LBC0I/pzbOU3r24t/c0XoOFEPSyKd7v31I4v65TOvhC3BkR9LK8etIecnMU9qe38fPgQQh6GRTzj9Oq1gcMUS+MoJdBx4iuoyNK9JyYC0HnpiDbRkeQz+9T+voxpZ9n1Z2N0f3R8w7NXnDfIyDo3E6PqzIb09x5px1LOHp0RtA56bJcNNqVo/WKWi8Im6OdPFqPmQg6Jx0lolG40XppO29HazETQecUHSFm7bYfXlcLG8OLw04IOqdo2o4bNe3e0cx6HEIEnUvb1Y15dtpoCLoTgs6lLWi9UIzW/y06quhqSbQWUxF0LtpRo4nWNkWX+gi6E4LOhaB7gaBzIeheIOhcCLoXCDoXgu4Fgs5lkaCjDyrpXcdoLaYi6Fy4Dt0LBJ1L16DbPtCkt8Sj9ZiKoHOKZtZOq4+KRsP3DDsh6Jyid/x0X7S2Fn3v0OVD/v8BQefU9vHRtt1W90fDC8LOCDqntvOwdunoM9HR5ToNx43OCDq3tkgVtc7LepGon9HxRKMP/EfPi7kQdG5tu/Q8o69jsTsvhKCXoe3KxazhUt3CCHpZHhI1/zkpG4JeJp2X287U9fC/7bIi6MegYHWc0JssNcU+7dvg6ISgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYeVP0Gebo1v9chcsAoZA7VZBXxXn49G+btxsr4SLgb5Tu5Ogy5Z15NjVjcstdmkMj5pVu5Ogy5aLk51irSz7qI5atRM2+k6NqtW/Yj5Wy4XmYlysl1EfVn8AhqXckNXwJOZ60kaxWv7hZfki8aBcdP3Pg4B+uZ60Wjardu8rLorfHp171ibGhLQAAAAASUVORK5CYII=",
                ).crop(80, 35, 21, 35),
                ImageCodec.fromBase64(
                    "iVBORw0KGgoAAAANSUhEUgAAALQAAABuCAYAAACOaDl7AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAM2SURBVHhe7dyxTttQFIfxiwd4CPY+SN8BqVMiJNSlSrp0rcQDtHuHbt0qdW5XpgwMwIrUB0iEypBIDQu39zh2i8JFgWAf2f9+R/qJ2LE9fbpyIpxQTzwIu7NhMZ4Nikn6O08i0GHzqtWxtVtlvJqrQdhPb56tnQD0Q2rXGi5jLlfmKubrwyIuj3bi7esQI9Bh1qi1as1WUZ9fjsJemA6LUR0zIaNvrNk6ams5VPchZe25E4Cus3arVXpiK/SNbbA6o6+s3SroZShfJLkDgb6oOyZoSCBoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoTx9exXjyNcafF/8cv8wfi60QtIdvH1fx5ubTm/w52ApBt8lC/jWtyn1gCLpRBN20dy9i/P55c8j1EHSjCLpJdo/8e1GV+sgh6EYRdJPsA95Th6AbRdBNy334s3327UZuCLpRBN00C7Se0x+r25D1/XeHoBtF0G348v7+98sE7YKgvRC0C4L2QtAuCNoLQbsgaC8E7YKgvRC0C4L2QtAuCNoLQbsgaC8E7YKgvRC0C4L2QtAuCNoLQbsgaC8E7YKgvRC0C4L2QtAuCNoLQbsg6DbYP/VbqHfZg7O5sf3rx9qDtrnrYiOCbsNTH5RdH/v5g9x1sRFBt+G5Y6t27rrYiKDb8Nwh6K0RdBse+yMzD409k5i7LjYiaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEghaEj5G/R0WNzYi9vMQUAfWLtV0PMwGxQT21ge7WQP/u8cZ/ah06zdMujUst1yjG3j+pBVGv1jzVq7ZdCp5XA5Cnup7PM6aqudsNF11qi1eifmC2s52FwNwn6K+qx6A+iXtCBbw2XM9cSDsJveeJs+JJ6mgxb3TgK6ZVG2mpq1dlcVh/AH7ZPTNudwnFQAAAAASUVORK5CYII=",
                ).crop(80, 35, 21, 35),
                ImageCodec.fromBase64(
                    "iVBORw0KGgoAAAANSUhEUgAAALQAAABuCAYAAACOaDl7AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAOuSURBVHhe7dsxS9xgHMfxxxv0Rbj3hfQ9CJ1OBOlStEvXgi+g3Tt061bo3K4dioODCp0E14IidVCoLj7NL5eUIz6hiUkudz++f/hQr5fL9PXhSS6GcuJWWL/cnuxfTieH2b83mQgssZui1X21W2Q8m6tp2MzePK58AFgNWbtqOI85X5mLmK93JvFudy0+vAwxAktMjapVNVtEfXK2FzbCxfZkr4yZkLFq1GwZtVoOxT4krz31AWDZqd1ilT7UCn2vF6zOWFVqtwj6LuQ/ZFIHAqui7JigYYGgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgYYWgl91B4v9Qi6BhhaBhhaBhhaBhhaBhhaAX5d2LGD+8mtHPqWPQGUEP5c2zGL+8j/H8NNbOzx+zwFOfx5MQ9BC+fozxz21RbYM5+jb7BUidC60QdJ8U5a/zotKWo9U6dU60QtB9Onhe1PnE0RYldV40RtB900pbHe2jtQ2R1Pvl/L5InxONEXTftEqX+2cFrNfVY3SXo26PzR2QTgh6CLpzkQp5nrYXqdEvQep4NELQY9EFZGoIuhOCHlNqCLoTgh4LK/QgCHosn94WBVeGi8JOCHos+nawOrrzkToWjRH0GOq+gGG70RlBjyH1wJJWZ57n6IygF02rcGpYnXtB0ItU9w2hHmhKHY/WCHpRtJ3QsxrVUeDc2egNQS9K3YP+PGHXK4JehO+fi3oro1t3qePxZAQ9tLqHkLRip45HJwQ9JO2NU6OLQG7RDYKghzL/XPT8cL95UAQ9BAWb+ttC7mgMjqCHUPdnVsQ8OILuW903gVqddSH4P7ojkjovGiHoPtVdBLYZRZ06Nxoh6D7V3aJrMwTdCUH3qW670WYIuhOC7hMr9OgIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlYIGlb+BX2xPbnXDw+Jg4BVoHaLoG/C5XRyqBd3u2vJg4Flp3bzoLOWteXY14vrHVZprB41q3bzoLOWw9le2MjKPimjVu2EjWWnRtXqXMynajlorqZhM4v6uHgDWC3ZgqyG85jLiVthPXvjdXaReJQddPvoQ8Byuc1bzZpVu7OKQ/gLUo76mqsaGfAAAAAASUVORK5CYII=",
                ).crop(80, 35, 21, 35),
                ImageCodec.fromBase64(
                    "iVBORw0KGgoAAAANSUhEUgAAALQAAABuCAYAAACOaDl7AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAPWSURBVHhe7dyxShxBAMbx8Qp9CPs8SN5BSHUihDRB06QN+ABJnyJdukDqpE0RLCxUSCXYBhSJhUK0cbLfupscl1kd3V339uM/8EPPm93qzzC3u2eoR1wLyyfrk62T6WSn+HleiMACO69a3VK7VcY343QaVos39+YOAMahaFcNlzGXK3MV89nGJF4+X4rXL0KMwAJTo2pVzVZR7x9uhpVwvD7ZrGMmZIyNmq2jVsuh2oeUtacOABad2q1W6R2t0Fd6weqMsVK7VdCXofylkJoIjEXdMUHDAkHDCkHDCkHDCkHDCkHDCkHDCkHDCkHDCkH3YTvxNzwKgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgoYVgn4Mr5/E+P7lP9tP0/PQGkH35e2zGL99ivHXcUyO3xcx7n4l7o4RdNcU6NFBVW3m+PwufS7cG0F37cuHqtJ7Dh2XOh/uhaC7phX6oUPblNQ5kY2g+6C9cT1+fL/ZUtQfCLWv1v45NTQ3dT5kI+g+6KqGthD6mXpfK3FT1Kn5yEbQQ2naa2sVT81HFoIeisJNDYJuhaCHQtC9IOih6MPh/NC+OjUX2Qh6CE0fChV5aj6yEfRj0nai6bLdz6PmqyLIRtB9+vimqvWOoZh5pqMTBN2nnNvgugmTOhYPQtB9yn2uQ1Gz3egEQfdJt7xzh/bVPMvRGkH3af7Bfq3YWo2bbntrL506D7IR9BAUuuJNDZ6NboWgh6KoUyu1vhyQmo8sBD2k2cdMZ0dqLrIQ9JCaroKk5iILQQ8p9TyHRmoushD0ULSHTn0jnCsdrRB017QvlttuZSvmpv2z/p46BlkIuku61jw7tNpqnzx/Lbrpf3VoaE7q3MhC0F3Sl1zbDC7ZtUbQXWozdE1aW5HUeZGNoLuk7UTTbe3bBo+Pdoagu6ZV9q59cj0UMre6O0XQfdKqW38QnKW/sSL3gqBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBhhaBh5W/Qx+uTK/1ynZgEjIHarYI+DyfTyY5eXD5fSk4GFp3aLYMuWtaWY0svzjZYpTE+albtlkEXLYfDzbBSlL1fR63aCRuLTo2q1ZmYD9Ry0DidhtUi6r3qDWBcigVZDZcx1yOuheXijVfFh8TdYtLFfwcBi+WibLVoVu3eVBzCHwTKBoR6KjUaAAAAAElFTkSuQmCC",
                ).crop(80, 35, 21, 35),
                ImageCodec.fromBase64(
                    "iVBORw0KGgoAAAANSUhEUgAAALQAAABuCAYAAACOaDl7AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAN6SURBVHhe7dsxTxRBGIfx4Qr4EPR+EL8DidUREmJjwMbWhA+gvYWdnYm1thbmCgogsSKhNYEQKSARGtZ5l1lzLi8Gb2Zvd/953uQXOW4uNo+Tnb01NFNthNWzzcnu2XQyi39eRhUwYJep1V1rN2V8N+fTsB7fPGh9ABiH2K41XMdc78wp5outSXW9vVLdPg9VBQyYNWqtWrMp6sPjnbAWTjcnO03MhIyxsWabqK3lkK5D6tq9DwBDZ+2mXXpmO/SNvWB3xlhZuyno61D/EHkLgbFoOiZoSCBoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCFoSCHokvac32GpCBpSCBpSCBpSCBpSCBpSCBpSCHpZ9r9U1cnR39698NdiYQS9DB9eV+7sPfXXY2EEvQzfv6WC58Z2aG8tshB012wX9ubTW389shB01z6/TwXPza8rfy2yEXTXLN72WOTeWmQj6C7ZZYU3HAY7Q9BdsoNfe+yA6K1FEQTdlYcOg9x77hRBd+Xrx1Tw3Pw89deiGILuCofBXhB0Fx46DL564q9HMQTdBe8waM9yzK+xr8Ntx27YPwLufmQj6NLePEsFt8Z+P7/OGy5JshF0abYTt+fHyf113hB0NoIuya6RvcOg99yGNwSdjaBL8g6DFrh3GPSGoLMRdEl2adEeux/trfWGoLMRdCkPHQYXHZ7IWwhBl2K7a+nx/h78E0GXQtCDQNClEPQgEHQp9s1f87+5H8Mbe3ipeb/9zSIehaD74g13ObIRdF+8IehsBN0Xbwg6G0H3xRuCzkbQffGGoLMRdF/sjkZ7vIeY8F8IGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlIIGlL+BH26ObmxH26dRcAYWLsp6MtwNp3M7MX19oq7GBg6a7cOOrZslxy79uJii10a42PNWrt10LHlcLwT1mLZh03UVjthY+isUWt1LuYjaznYnE/Deoz6IL0BjEvckK3hOuZmqo2wGt94GQ+J+3HR1b0PAcNyVbcam7V27yoO4TdppfBEmKTipgAAAABJRU5ErkJggg==",
                ).crop(80, 35, 21, 35),
                ImageCodec.fromBase64(
                    "iVBORw0KGgoAAAANSUhEUgAAALQAAABuCAYAAACOaDl7AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAOESURBVHhe7dqxTttQGIbhQwa4CPZeSO8BqVMQEupSQZeulbiAdu/QrVulzu3KlIEBWJFYK4FQGUAqLLjnc+wqoifgOD51/On9pUeNie3p7ZFjO9RTbIX1y+3R/uV4NIn/3kQFsMJuqlb31W6V8XSuxmEzfnn86ABgGGK7ariMuVyZq5ivd0bF3e5a8fA6FAWwwtSoWlWzVdQnZ3thI1xsj/bqmAkZQ6Nm66jVcqiuQ8raUwcAq07tVqv0RCv0vTZYnTFUarcK+i6UH6LUjsBQ1B0TNCwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNIbpIPG3iKBhhaBhhaBz+P65KM5P23n3In1ONELQOSjMtvPpTfqcaISgcyDo3hB0DgTdG4LOYZmgD16mz4lGCDqHVNCsvP8FQedA0L0h6BwIujcEnQNB94agcyDo3hB0Dqmgj35MnyDWCDwLgs4hFfS8UejcqusMQeewSNCa37dF8e1j+lxYCEHnsGjQ9Xx5nz4fGiPoHGaD/nk+3RZ9fmq0UvO23VIIOgddE8+7Llaw+lGoeFOj71LHoRGC7suHV1XBj+bXRXp/NELQfdIdjtRw2dEaQfdJdzZSwz3q1gi6Two3NQTdGkH3iaA7R9B90h2N1HAN3RpB90XR6o7G4+Eux1IIumuHX6cPUZ56P0Mxz7vDoeNTx6ARgu6SIp4dPRms36yraTu1MtfDi0pLIeguzVt1mw5PCZdG0F16auV9bvSfIXVOLISgu6RLijZRc93cGYLOQU8An3uzTqNVWe90pM6BVgg6J/3Aq38IztLfUvtjaQQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNK3+Dvtge3evDQ2InYAjUbhX0TbgcjybauNtdS+4MrDq1WwYdW9Ylx742rndYpTE8albtlkHHlsPZXtiIZZ/UUat2wsaqU6NqdSbmU7UcNFfjsBmjPq6+AIYlLshquIy5nmIrrMcv3sYfiUdxp9t/DgJWy23ZamxW7U4rDuEPSxgaoiy7GdgAAAAASUVORK5CYII=",
                ).crop(80, 35, 21, 35),
                ImageCodec.fromBase64(
                    "iVBORw0KGgoAAAANSUhEUgAAALQAAABuCAYAAACOaDl7AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAPySURBVHhe7duxThRdHIbxYQu4CHovxHsgsVpCQmwM2NiacAHaW9jZmVhra2EoKIDEioTWBEKkgERoGOcdZnSz/mfZnTmDO2+ek/zyLbtnt3q+kzNnxqwe+Ua2er452j0fj/aL/14VcmCJXVWt7qrdKuP7cTHO1osPD6e+AAxD0a4aLmMuV+Yq5sutUX6zvZLfPc/yHFhialStqtkq6qOTnWwtO9sc7dQxEzKGRs3WUavlrNqHlLVHXwCWndqtVul9rdC3+oPVGUOldqugb7LyRSGaCAxF3TFBwwJBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpBwwpB/w/vXvz16kk8B60Q9GP59DbPf5zm4fh5luef3xN3AgTdtzfPmkOeHr+u71ft6HcwF4Luk2JWpIsMrdTRb2EuBN2XNjFrPtuOTgi6L03bDL2vVbi+KNTrei6rc2cE3QddAEbj4Es8X7SiR+9jIQTdB51aTA+twtFcJEXQqX14XRU8NfR+NB9JEXRq2lZMD13sRXORHEGnFp1szNo7IymCTkkXdtGYPL2oTzZq3EhJiqBTmrV/VrxN59J6X59zBt0ZQaekKKMRnXpEQychRN0JQaf09WNVZofB8V4nBJ3S6XFVZcehlT76fTyIoFNqCrp+PHTybqBef/9WTZgamj/5u5gbQacUBf3QFqLpfwJuhbdC0ClFceq9aG6t6WREz4NE8zETQafUJmiJBvvoVgg6peiUY579cDQIuhWCTqnpHDqaOykaBN0KQaek29jRmPWkXdN3uCXeCkGnpLt80Zj1cFL0dJ4GdwxbIejUFjmGa3qYiafzWiPo1Jr++ZUeQKqP4rT66nXTw0psN1oj6D40rdLzDJ2URL+JuRB0H7SVaFp9Zw0eTOqMoPuyaNTaN3Mh2BlB90mBKtRZYWtVZs+cDEE/FkWrmyU1XRTuPY3nojWChhWChhWChhWChhWChhWChhWChhWChhWChhWChhWChhWChhWChhWChhWChhWChhWCxvDsBe9VCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpWCBpW/gR9tjm61Yu7YBIwBGq3CvoqOx+P9vXHzfZKOBlYdmq3DLpoWVuOXf1xucUqjeFRs2q3DLpoOTvZydaKso/qqFU7YWPZqVG1OhHzsVrONC7G2XoR9WH1ATAsxYKshsuY65FvZKvFBy+Li8SDYtL1P18Clst12WrRrNq9rzjLfgPvV2WyxU3SigAAAABJRU5ErkJggg==",
                ).crop(80, 35, 21, 35),
                ImageCodec.fromBase64(
                    "iVBORw0KGgoAAAANSUhEUgAAALQAAABuCAYAAACOaDl7AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAN1SURBVHhe7ds/S1tRGMfxYwZ9Ee59IX0PQqeIIF1K7NK14Ato9w7duhU6t2uHksFBhU6Ca8EgdVCoLt6e53pSQvpEjTnn/vnxfeCDxpzo8uVwk3sM06m2wvpke7A3GQ7G8etlVAEddpla3bN2U8Z3cz4Mm/HJw7kXAP0Q27WG65jrnTnFfLEzqK5316rbl6GqgA6zRq1VazZFfXQyChvhbHswmsZMyOgba3YatbUc0nVIXbv3AqDrrN20S49th76xB+zO6CtrNwV9HepvIm8h0BfTjgkaEroT9L7zM2BJ3QkayICgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgIYWgc9p/XlWnx6v7+aOq3jzz/wbuRdA5fXhVZRv7Xd7fwL0IOieCbh1B55QzaC45noSgc3r3ItW44vw69X8/HkTQbft9liqemS/v/bV4EEG36dPbVPDM/LnicmMFBN0m+4hufg6++WvxKATdlkXX2/Zzbz0ehaDbYjvx/NiO7a3FoxF0G+wa2RveDK6MoNvw9WMqeGbszaC3Fksh6DZYvPNjkXtrsRSCbppdVnhjB5u89VgKQTfNu5Fip+u8tVgaQTdp0VkPDiJlQ9BN8m6k2I7trcWTEHRT7BrZG94MZkXQTfFupHBuIzuCboJF631Ux7mN7Ai6Cd6NFBvObWRH0E3wPqrjEH8RBF3aohspnNsogqBLs514fji3UQxBl7ToRsr3z/56rIygS7Jb2t5wbqMYgi5l0Y0Uzm0URdCl2GWFN/aPsd56ZEHQpXgf1fEvVsURNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKQQNKT8C/pse3Bj39w6i4A+sHZT0JdhMhyM7cH17pq7GOg6a7cOOrZslxx79uBih10a/WPNWrt10LHlcDIKG7Hso2nUVjtho+usUWt1JuZjaznYnA/DZoz6MD0B9EvckK3hOubpVFthPT7xOr5JPIiLrv57EdAtV3WrsVlr967iEP4C8Cq2hNrkePwAAAAASUVORK5CYII=",
                ).crop(80, 35, 21, 35),
                ImageCodec.fromBase64(
                    "iVBORw0KGgoAAAANSUhEUgAAALQAAABuCAYAAACOaDl7AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAQMSURBVHhe7duxShxRGIbhcQu9CPtcSO5BSLUiSJqgadIGvICkT5EuXSB10qYIFhYqpBJsA4rEQiHaOJlvnUmW9T/u7OwZ2f14f3iIurNTvRzOntkUzZQbxer55mD3fDjYr/69qpTAAruqW91Vu3XG93MxLNarFw8n3gAsh6pdNTyKebQy1zFfbg3Km+2V8u5lUZbAAlOjalXN1lEfnewUa8XZ5mCniZmQsWzUbBO1Wi7qfcio9ugNwKJTu/Uqva8V+la/sDpjWandOuibYvRDJboQWBZNxwQNCwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwQNKwT9lPael+WHV/9F12AuBN03RXzwrSx/n5Xh/Doty68fy/LNs/j9mAlB90mhth0F/+5FfB+0RtB90ao86/y5Juo5EXQfPr2tC+0w2oJE90QrBN2H1H5ZW5DmGkWvFTkavTZ+P7RG0LlpyxDNeMzTrv3++eG1aIWgc0t9EEydYmiLMTmnx/G1mIqgc0sFHV0rindyCLozgs4tFXRqXxzto9lydEbQuaVOOLS1mNx2pOLnKWJnBJ2bngymRqcfTaz6QBitzj9/PLwnWiPoPkx7qKI9chRztIpjJgTdB63SqTPm1GhlJua5EXRftLVoGzUrc9pe8LdHEHSfdFrRdhQ/HwbnRtB90GobnS+3mS/v43uiFYLOTTFHT//0Nx3ptQmdb9x1RtC5RcFO7pG1tXgsbL02fk+0RtA5absQjU49outTD1Y0qffgUQSdk47eJmfaapuKmr10JwSdU3RMp2CjaxvaikQz7X0IEXRO0bQJMxqC7oSgc4pm2pZDHxCjIehOCDqn6LhOk9oPp474NBzddULQOWlVTY1WaoWtFVn0FDH1aFzfyovuj6kIOietuLN+KSka/pNsZwSdW+oL/m1HXz2N7otWCLoPirrLSs0HwbkRdF+0/VCg08LW61qVeTKYBUE/BZ1Y6AOhAm/od04ysiNoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWCFoWPkX9Nnm4FY/3AUXActA7dZBXxXnw8G+frnZXgkvBhad2h0FXbWsLceufrncYpXG8lGzancUdNVycbJTrFVlHzVRq3bCxqJTo2p1LOZjtVxoLobFehX1Yf0CsFyqBVkNj2JuptwoVqsXXlcfEg+qi64fvAlYLNejVqtm1e59xUXxF7FrqcsXD2f6AAAAAElFTkSuQmCC",
                ).crop(80, 35, 21, 35),
                ImageCodec.fromBase64(
                    "iVBORw0KGgoAAAANSUhEUgAAALQAAABuCAYAAACOaDl7AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAP7SURBVHhe7dy9ThRRHIbxwxZwEfReiPdAYgUhITYGbGxNuADtLezsTKy1tTAUFEBiRUJrAiFSQCI0jPMuM0rW/+zszp5xd988J/kFlj2z1ePJmY811aPYSKsXW4O9i83BQfnzulQAC+y6anVP7VYZP4zLzbRevnk0cgCwHMp21fAw5uHKXMV8tT0obndWivvnqSiABaZG1aqaraI+Pt1Na+l8a7Bbx0zIWDZqto5aLadqHzKsPToAWHRqt1qlD7RC3+kFqzOWldqtgr5Nw19K0URgWdQdEzQsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEDSsEPT/8upJUbx78ZdeR/MwE4Luk6L9/L4ofp4X4dDf9T5xZ0PQffnwuih+3VTltgzN+/Q2/hxMhaD7oDi7DKKeGUHn9uZZVWfHoeOjz8VECDq3s5OqzJHx4+xhBdYJoX7qdTR0fPS5mAhB56RYo/H9Wzy/KX5W6c4IOqevH6siR0bTVYz9p9WEkaErH9F8tCLonKJtRNPqXIuOYdvRGUHnFI221bZpVY/mohVB5xSNtqD1fjSiuWhF0DlF4/BLPLfWdCKpv0fzMRZB5xTth3UXcNytbYLOiqBzatoPa5WOotZVDp00RoOgOyHonJouw2lopVbY2jMr/KYbK/Ug6E4IOremVXraQdCdEHQftBLPOrhb2AlB90Vbi7bHRxV+05N50WeiFUH3SSeCClbh6u6f6CRQsWu/rTnRdWj9Qxj9LEyEoOct2nNz67szgp636Ik7RR7NRSuCnidtSaKhr29F89GKoOcpOiFk/zwTgp6n6NvgOoGM5mIiBD0vTdequaEyE4Lug6LUULTaDz9+jkOvoxNBjbYvA6AVQfeh6T+WGTe0d66vTaMzgs5NK3CXwZWNLAg6t2mD1so8zb55P/gb/iDoPmjroP3zuGc59J7mjHv4H1Mj6L5p9dXzGo9xJaM3BA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rBA0rf4I+3xrc6Zf7YBKwDNRuFfR1utgcHOjF7c5KOBlYdGp3GHTZsrYce3pxtc0qjeWjZtXuMOiy5XS6m9bKso/rqFU7YWPRqVG1+ijmE7WcNC4303oZ9VH1BrBcygVZDQ9jrkexkVbLN16WJ4mH5aSbfw4CFsvNsNWyWbX7UHFKvwG5VmB291xAGAAAAABJRU5ErkJggg==",
                ).crop(80, 35, 21, 35),
            )
    }

    private val DEFAULT_SELECTOR_WAIT_OPTIONS = Page.WaitForSelectorOptions().setTimeout(5000.0)
    private val DEFAULT_WAIT_TIME = 5000L

    private suspend inline fun <T> withLoggedInSession(
        permanent: Boolean = false,
        task: (context: BrowserContext, page: Page) -> T,
    ): T {
        val context = allocator.acquire()
        val page = context.newPage()
        page.navigateAsync("https://www.ing.com.au/securebanking/")
        try {
            page.waitForResponseAsync(
                "https://www.ing.com.au/KeypadService/v1/KeypadService.svc/json/PinpadImages",
                Page.WaitForResponseOptions().setTimeout(10000.0),
            )
            page.waitForResponseAsync(
                "https://www.ing.com.au/KeypadService/v1/KeypadService.svc/json/PinpadImages",
                Page.WaitForResponseOptions().setTimeout(3000.0),
            )
        } catch (_: TimeoutError) {
            // This wait is flakey sometimes
        }
        page.waitForSelectorAsync("#cifField", DEFAULT_SELECTOR_WAIT_OPTIONS)

        page.locator("#cifField").fill(username)
        delay(50)
        val keys = page.querySelector("#keypad > div").querySelectorAll("[role=\"button\"]")

        for (digit in password) {
            for (key in keys) {
                val imageB64 = key.querySelector("img").getAttribute("src").split(",")[1]
                val image = ImageCodec.fromBase64(imageB64).crop(80, 35, 21, 35)
                // Check if key img similarity is high
                if (image.compare(IMAGE_REFERENCES[digit]) > 0.95) {
                    key.click()
                    break
                }
            }
            delay(50)
        }

        page.locator("#login-btn").click()

        val responseJson =
            Json.decodeFromString<JsonObject>(
                page.waitForResponseAsync("https://www.ing.com.au/api/token/login/issue").text(),
            )
        if (responseJson["ErrorMessage"] != null) {
            throw CredentialException(responseJson.throwIfNullKey("ErrorMessage").jsonPrimitive.content)
        }

        val result = task(context, page)
        if (!permanent) {
            allocator.release(context)
        }
        return result
    }

    private fun parseAccountsFromJsonString(jsonString: String): List<Account> {
        val rootObj = Json.decodeFromString<JsonObject>(jsonString)
        val categories = rootObj.throwIfNullKey("Response").jsonObject.throwIfNullKey("Categories").jsonArray
        return categories.map { Account.fromINGJson(it.jsonObject.throwIfNullKey("Accounts").jsonArray[0].jsonObject) }
    }

    private fun parseTransactionsFromJsonString(jsonString: String): List<BankTransaction> {
        val rootObj = Json.decodeFromString<JsonObject>(jsonString)
        val transactions = rootObj.throwIfNullKey("Response").jsonObject.throwIfNullKey("Transactions").jsonArray
        return transactions.map { BankTransaction.fromINGJson(it.jsonObject) }
    }

    private suspend fun navigateToAccount(
        id: String,
        page: Page,
    ) {
        val response =
            page.waitForResponseAsync(
                "https://www.ing.com.au/api/Dashboard/Service/DashboardService.svc/json/Dashboard/loaddashboard",
            )
        if (!response.isSuccess()) {
            throw ServerException("Unknown ING Accounts API error. Status: ${response.status()}, Data: ${response.body()}")
        }

        val tableEntries = page.querySelectorAll("[class=\"module-wrap style-scope ing-all-accounts-summary\"]")
        val account =
            tableEntries.filter {
                val bsb = it.querySelector("[class=\"uia-account-bsb style-scope ing-all-accounts-summary\"]").innerHTML()
                val accountNum = it.querySelector("[class=\"uia-account-number style-scope ing-all-accounts-summary\"]").innerHTML()
                return@filter "$bsb $accountNum" == id
            }
        account[0].click()
    }

    override suspend fun getAccounts(): List<Account> {
        return withLoggedInSession { _, page ->
            val response =
                page.waitForResponseAsync(
                    "https://www.ing.com.au/api/Dashboard/Service/DashboardService.svc/json/Dashboard/loaddashboard",
                )
            if (response.isSuccess()) {
                return@withLoggedInSession parseAccountsFromJsonString(response.text())
            }
            throw ServerException("Unknown ING Accounts API error. Status: ${response.status()}, Data: ${response.body()}")
        }
    }

    override suspend fun getTransactions(
        accountId: String,
        limit: Int,
    ): List<BankTransaction> {
        return withLoggedInSession { _, page ->
            navigateToAccount(accountId, page)
            val response =
                page.waitForResponseAsync(
                    "https://www.ing.com.au/api/AccountDetails/Service/AccountDetailsService.svc/json/accountdetails/AccountDetails",
                )
            if (response.isSuccess()) {
                val transactions = parseTransactionsFromJsonString(response.text()).toMutableList()
                while (limit > transactions.size) {
                    try {
                        page.waitForSelectorAsync(
                            "#transactionsList > div.row.no-margin-left.no-margin-right.style-scope.ing-account-transaction-list > div > div > button",
                            Page.WaitForSelectorOptions().setTimeout(3000.0),
                        ).click()
                    } catch (e: TimeoutError) {
                        break
                    }

                    try {
                        val response_ =
                            page.waitForResponseAsync(
                                "https://www.ing.com.au/api/TransactionHistory/Service/TransactionHistoryService.svc/json/TransactionHistory/TransactionHistory",
                                Page.WaitForResponseOptions().setTimeout(5000.0),
                            )
                        if (response_.isSuccess()) {
                            transactions.addAll(parseTransactionsFromJsonString(response_.text()))
                        } else {
                            throw ServerException(
                                "Unknown ANZ Transactions API error. Status: ${response.status()}, Data: ${response.body()}",
                            )
                        }
                    } catch (_: TimeoutError) {
                        // No-Op
                    }
                }
                if (transactions.size > limit) {
                    return@withLoggedInSession transactions.dropLast(transactions.size - limit)
                }
                return@withLoggedInSession transactions
            }
            throw ServerException("Unknown ING Transactions API error. Status: ${response.status()}, Data: ${response.body()}")
        }
    }

    override suspend fun getRealTimeTransactions(accountId: String): Flow<BankTransaction> {
        return withLoggedInSession(true) { context, page ->
            navigateToAccount(accountId, page)

            val response =
                page.waitForResponseAsync(
                    "https://www.ing.com.au/api/AccountDetails/Service/AccountDetailsService.svc/json/accountdetails/AccountDetails",
                )
            if (!response.isSuccess()) {
                throw ServerException("Unknown ING Transactions API error. Status: ${response.status()}, Data: ${response.body()}")
            }

            val results = parseTransactionsFromJsonString(response.text())
            val idSet: MutableSet<String> = HashSet(results.map { it.id })
            return@withLoggedInSession flow {
                var lastUpdatedTime: Long = 0
                while (true) {
                    if (System.currentTimeMillis() - lastUpdatedTime < DEFAULT_WAIT_TIME) {
                        delay(DEFAULT_WAIT_TIME - (System.currentTimeMillis() - lastUpdatedTime))
                    }
                    lastUpdatedTime = System.currentTimeMillis()

                    page.waitForSelector("#mainMenuList > li:nth-child(3) > div", Page.WaitForSelectorOptions().setTimeout(3000.0)).click()
                    navigateToAccount(accountId, page)

                    val newResponse =
                        page.waitForResponseAsync(
                            "https://www.ing.com.au/api/AccountDetails/Service/AccountDetailsService.svc/json/accountdetails/AccountDetails",
                        )
                    if (!newResponse.isSuccess()) {
                        allocator.release(context)
                        throw ServerException("Unknown ING Transactions API error. Status: ${response.status()}, Data: ${response.body()}")
                    }

                    val newResults = parseTransactionsFromJsonString(newResponse.text()).filter { !idSet.contains(it.id) }
                    newResults.forEach { emit(it) }
                    idSet.addAll(newResults.map { it.id })
                }
            }
        }
    }
}
