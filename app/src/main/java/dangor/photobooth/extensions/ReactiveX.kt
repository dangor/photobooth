package dangor.photobooth.extensions

import io.reactivex.Observable
import io.reactivex.functions.BiFunction

fun <S, O, R> Observable<S>.withLatestFrom(other: Observable<O>, combiner: (S, O) -> R): Observable<R> {
    return this.withLatestFrom(other, BiFunction(combiner))
}