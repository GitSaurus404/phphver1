package jp.ac.titech.c.phph.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.Map;

/**
 * A normalized fragment of source code.
 */
@Value
@AllArgsConstructor
@EqualsAndHashCode(of = {"hash"})
public class Pattern {
    @Getter
    Hash oldHash;

    @Getter
    Hash newHash;

    @Getter
    Hash preContext; // NYI

    @Getter
    Hash postContext; // NYI

    @Getter
    Hash hash;

    @Getter
    Map<String, Object> metrics;

    public Pattern(final Hash oldHash, final Hash newHash, final Hash preContext, final Hash postContext, final Hash hash) {
        this(oldHash, newHash, preContext, postContext, hash, null);
    }

    public static Pattern of(final Hash oldHash, final Hash newHash, final Hash preContext, final Hash postContext, final Hash hash) {
        return new Pattern(oldHash, newHash, preContext, postContext, hash);
    }

    public static Pattern of(final Hash oldHash, final Hash newHash, final Hash hash) {
        return new Pattern(oldHash, newHash, Hash.ZERO, Hash.ZERO, hash);
    }

    public static Pattern of(final Hash oldHash, final Hash newHash, final Hash preContext, final Hash postContext) {
        return new Pattern(oldHash, newHash, preContext, postContext, digest(oldHash, newHash, preContext, postContext));
    }

    public static Pattern of(final Hash oldHash, final Hash newHash) {
        return of(oldHash, newHash, Hash.ZERO, Hash.ZERO);
    }

    public static Pattern of(final Fragment oldFragment, final Fragment newFragment, final Fragment preContext, final Fragment postContext) {
        return of(oldFragment.getHash(), newFragment.getHash(), preContext.getHash(), postContext.getHash());
    }

    public static Pattern of(final Fragment oldFragment, final Fragment newFragment) {
        return of(oldFragment.getHash(), newFragment.getHash(), Hash.ZERO, Hash.ZERO);
    }

    @Override
    public String toString() {
        return hash + ":" + getType().name() + "|" + oldHash + "->" + newHash;
    }

    public String toShortString() {
        return hash.abbreviate(6) + ":" + getType().name().charAt(0) + "|" + oldHash.abbreviate(6) + "->" + newHash.abbreviate(6);
    }

    /**
     * Computes SHA1 from a string.
     */
    public static Hash digest(final Hash... hashes) {
        return Hash.of(md -> {
            for (final Hash h : hashes) {
                if (h != null) {
                    md.update(h.getRaw());
                } else {
                    md.update((byte) 0);
                }
            }
        });
    }

    public ChangeType getType() {
        if (oldHash.isZero()) {
            return newHash.isZero() ? ChangeType.EMPTY : ChangeType.ADD;
        } else {
            return newHash.isZero() ? ChangeType.DELETE : ChangeType.MODIFY;
        }
    }
}
